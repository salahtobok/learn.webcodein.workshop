package com.webcodein.locking.service;

import com.webcodein.locking.entity.Product;
import com.webcodein.locking.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final ProductRepository productRepository;
    private final OptimisticLockingTransactionWorker transactionWorker;

    public InventoryService(ProductRepository productRepository,
                            OptimisticLockingTransactionWorker transactionWorker) {
        this.productRepository = productRepository;
        this.transactionWorker = transactionWorker;
    }

    @Transactional
    public Product decrementStockOptimistic(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        product.decrementStock(quantity);
        return productRepository.saveAndFlush(product);
    }

    public Product decrementStockOptimisticWithRetry(Long productId, int quantity, int maxRetries) {
        int attempts = 0;
        while (attempts < maxRetries) {
            attempts++;
            try {
                return transactionWorker.executeSingleAttempt(productId, quantity);
            } catch (ObjectOptimisticLockingFailureException ex) {
                log.warn("Optimistic lock conflict on attempt {} for product {}. Retrying...", attempts, productId);
                if (attempts >= maxRetries) {
                    throw ex;
                }
                backoff(attempts);
            }
        }
        throw new IllegalStateException("Max retries exceeded for product " + productId);
    }

    @Transactional
    public Product decrementStockPessimistic(Long productId, int quantity) {
        Product product = productRepository.findByIdWithPessimisticWriteLock(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        product.decrementStock(quantity);
        return productRepository.saveAndFlush(product);
    }

    private void backoff(int attempt) {
        try {
            long baseDelay = 15L * attempt;
            long jitter = ThreadLocalRandom.current().nextLong(5, 25);
            Thread.sleep(baseDelay + jitter);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted during optimistic backoff", e);
        }
    }
}
