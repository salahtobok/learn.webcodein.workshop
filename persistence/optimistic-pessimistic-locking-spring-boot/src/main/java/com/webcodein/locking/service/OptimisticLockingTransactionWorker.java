package com.webcodein.locking.service;

import com.webcodein.locking.entity.Product;
import com.webcodein.locking.repository.ProductRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OptimisticLockingTransactionWorker {

    private final ProductRepository productRepository;

    public OptimisticLockingTransactionWorker(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Product executeSingleAttempt(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        product.decrementStock(quantity);
        return productRepository.saveAndFlush(product);
    }
}
