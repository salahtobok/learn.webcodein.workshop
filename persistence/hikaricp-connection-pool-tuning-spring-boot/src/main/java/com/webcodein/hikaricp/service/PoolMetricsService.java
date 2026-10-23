package com.webcodein.hikaricp.service;

import com.webcodein.hikaricp.dto.PoolMetricsResponse;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class PoolMetricsService {

    private static final Logger log = LoggerFactory.getLogger(PoolMetricsService.class);

    private final DataSource dataSource;

    public PoolMetricsService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PoolMetricsResponse getPoolMetrics() {
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            HikariPoolMXBean poolMxBean = hikariDataSource.getHikariPoolMXBean();
            int active = poolMxBean != null ? poolMxBean.getActiveConnections() : 0;
            int idle = poolMxBean != null ? poolMxBean.getIdleConnections() : 0;
            int total = poolMxBean != null ? poolMxBean.getTotalConnections() : 0;
            int waiting = poolMxBean != null ? poolMxBean.getThreadsAwaitingConnection() : 0;

            log.debug("HikariCP Metrics - Pool: {}, Active: {}, Idle: {}, Total: {}, Waiting: {}",
                    hikariDataSource.getPoolName(), active, idle, total, waiting);

            return new PoolMetricsResponse(
                    hikariDataSource.getPoolName(),
                    active,
                    idle,
                    total,
                    waiting,
                    hikariDataSource.getMaximumPoolSize(),
                    hikariDataSource.getMinimumIdle()
            );
        }

        return new PoolMetricsResponse("Unknown", 0, 0, 0, 0, 0, 0);
    }
}
