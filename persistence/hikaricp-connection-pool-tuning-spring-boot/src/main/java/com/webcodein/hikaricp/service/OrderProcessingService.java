package com.webcodein.hikaricp.service;

import com.webcodein.hikaricp.dto.OrderRequest;
import com.webcodein.hikaricp.entity.ProductOrder;
import com.webcodein.hikaricp.repository.ProductOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OrderProcessingService {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessingService.class);

    private final ProductOrderRepository orderRepository;
    private final DataSource dataSource;
    private final List<Connection> leakedConnectionStore = Collections.synchronizedList(new ArrayList<>());

    public OrderProcessingService(ProductOrderRepository orderRepository, DataSource dataSource) {
        this.orderRepository = orderRepository;
        this.dataSource = dataSource;
    }

    @Transactional
    public ProductOrder createOrder(OrderRequest request) {
        log.info("Creating order for customer: {}", request.customerName());
        ProductOrder order = new ProductOrder(
                request.customerName(),
                request.productCode(),
                request.amount(),
                "CONFIRMED",
                Instant.now()
        );
        return orderRepository.save(order);
    }

    @Transactional
    public void processSlowOrder(double delaySeconds) {
        log.info("Executing slow database operation for {} seconds", delaySeconds);
        orderRepository.executeSimulatedDatabaseDelay(delaySeconds);
        log.info("Completed slow database operation");
    }

    /**
     * Intentionally borrows a raw Connection from the pool and does not close it.
     * HikariCP leakDetectionThreshold (2000 ms) will trigger a warning log with stack trace.
     */
    public String simulateConnectionLeak() {
        try {
            log.warn("Simulating connection leak: Borrowing connection and keeping it open");
            Connection connection = dataSource.getConnection();
            leakedConnectionStore.add(connection);
            return "Connection checked out without closing. Leak watchdog will trigger in 2000ms.";
        } catch (SQLException e) {
            log.error("Failed to borrow connection from pool", e);
            throw new RuntimeException("Could not borrow connection from pool", e);
        }
    }

    public void releaseLeakedConnections() {
        log.info("Releasing {} leaked connections", leakedConnectionStore.size());
        synchronized (leakedConnectionStore) {
            for (Connection conn : leakedConnectionStore) {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    log.error("Error closing leaked connection", e);
                }
            }
            leakedConnectionStore.clear();
        }
    }
}
