package com.webcodein.locking;

import com.webcodein.locking.entity.JobTicket;
import com.webcodein.locking.entity.Product;
import com.webcodein.locking.repository.JobTicketRepository;
import com.webcodein.locking.repository.ProductRepository;
import com.webcodein.locking.service.InventoryService;
import com.webcodein.locking.service.JobQueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class LockingIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JobTicketRepository jobTicketRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private JobQueueService jobQueueService;

    @BeforeEach
    void cleanDatabase() {
        productRepository.deleteAll();
        jobTicketRepository.deleteAll();
    }

    @Test
    @DisplayName("1. Optimistic Locking: Should detect version mismatch and throw ObjectOptimisticLockingFailureException")
    void shouldThrowOptimisticLockExceptionOnVersionMismatch() {
        Product initial = productRepository.saveAndFlush(new Product("Mechanical Keyboard", 50));
        Long productId = initial.getId();

        // Transaction 1 loads entity at version 0
        Product tx1Product = productRepository.findById(productId).orElseThrow();

        // Transaction 2 loads entity at version 0
        Product tx2Product = productRepository.findById(productId).orElseThrow();

        // Transaction 1 modifies and commits (increments version to 1)
        tx1Product.decrementStock(5);
        productRepository.saveAndFlush(tx1Product);

        // Transaction 2 tries to commit stale state (version 0 instead of current version 1)
        tx2Product.decrementStock(10);
        assertThatThrownBy(() -> productRepository.saveAndFlush(tx2Product))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);

        // Verify state: only Transaction 1 updates persisted
        Product refreshed = productRepository.findById(productId).orElseThrow();
        assertThat(refreshed.getStock()).isEqualTo(45);
        assertThat(refreshed.getVersion()).isEqualTo(1L);
    }

    @Test
    @DisplayName("2. Optimistic Locking with Retry: High concurrency resolves all updates without lost updates")
    void shouldResolveConcurrentUpdatesUsingOptimisticRetry() throws InterruptedException {
        final int initialStock = 20;
        final int concurrentThreads = 20;
        Product product = productRepository.saveAndFlush(new Product("Gaming Mouse", initialStock));
        final Long productId = product.getId();

        ExecutorService executor = Executors.newFixedThreadPool(concurrentThreads);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(concurrentThreads);
        AtomicInteger successCounter = new AtomicInteger(0);

        for (int i = 0; i < concurrentThreads; i++) {
            executor.submit(() -> {
                try {
                    startGate.await();
                    inventoryService.decrementStockOptimisticWithRetry(productId, 1, 30);
                    successCounter.incrementAndGet();
                } catch (Exception e) {
                    // Log or handle unexpected failure
                } finally {
                    endGate.countDown();
                }
            });
        }

        startGate.countDown();
        boolean completed = endGate.await(15, TimeUnit.SECONDS);
        executor.shutdownNow();

        assertThat(completed).isTrue();
        assertThat(successCounter.get()).isEqualTo(concurrentThreads);

        Product finalProduct = productRepository.findById(productId).orElseThrow();
        assertThat(finalProduct.getStock()).isEqualTo(0);
        assertThat(finalProduct.getVersion()).isEqualTo((long) concurrentThreads);
    }

    @Test
    @DisplayName("3. Pessimistic Write Locking: Database row lock serializes high concurrency without retries")
    void shouldSerializeConcurrentUpdatesUsingPessimisticWriteLock() throws InterruptedException {
        final int initialStock = 20;
        final int concurrentThreads = 20;
        Product product = productRepository.saveAndFlush(new Product("UltraWide Monitor", initialStock));
        final Long productId = product.getId();

        ExecutorService executor = Executors.newFixedThreadPool(concurrentThreads);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(concurrentThreads);
        AtomicInteger successCounter = new AtomicInteger(0);

        for (int i = 0; i < concurrentThreads; i++) {
            executor.submit(() -> {
                try {
                    startGate.await();
                    inventoryService.decrementStockPessimistic(productId, 1);
                    successCounter.incrementAndGet();
                } catch (Exception e) {
                    // Log failure if any
                } finally {
                    endGate.countDown();
                }
            });
        }

        startGate.countDown();
        boolean completed = endGate.await(15, TimeUnit.SECONDS);
        executor.shutdownNow();

        assertThat(completed).isTrue();
        assertThat(successCounter.get()).isEqualTo(concurrentThreads);

        Product finalProduct = productRepository.findById(productId).orElseThrow();
        assertThat(finalProduct.getStock()).isEqualTo(0);
        assertThat(finalProduct.getVersion()).isEqualTo((long) concurrentThreads);
    }

    @Test
    @DisplayName("4. Pessimistic SKIP LOCKED: Multiple workers claim distinct jobs concurrently without waiting")
    void shouldProcessQueueWithoutBlockingUsingSkipLocked() throws InterruptedException {
        final int totalJobs = 5;
        for (int i = 1; i <= totalJobs; i++) {
            jobTicketRepository.save(new JobTicket("PAYLOAD_" + i, "PENDING"));
        }

        ExecutorService executor = Executors.newFixedThreadPool(totalJobs);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(totalJobs);
        List<Long> claimedJobIds = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < totalJobs; i++) {
            executor.submit(() -> {
                try {
                    startGate.await();
                    Optional<JobTicket> claimed = jobQueueService.claimAndProcessNextJob();
                    claimed.ifPresent(ticket -> claimedJobIds.add(ticket.getId()));
                } catch (Exception e) {
                    // Ignore
                } finally {
                    endGate.countDown();
                }
            });
        }

        startGate.countDown();
        boolean completed = endGate.await(10, TimeUnit.SECONDS);
        executor.shutdownNow();

        assertThat(completed).isTrue();
        assertThat(claimedJobIds).hasSize(totalJobs);
        // Ensure all claimed job IDs are distinct (no two threads claimed the same job)
        assertThat(claimedJobIds.stream().distinct().count()).isEqualTo(totalJobs);

        List<JobTicket> remainingPending = jobTicketRepository.findByStatus("PENDING");
        assertThat(remainingPending).isEmpty();
    }
}
