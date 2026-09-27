package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.application;

import com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductApplicationService {

    private final ProductRepository productRepository;

    public ProductApplicationService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Use case: Create a new product
    public ProductId createProduct(CreateProductCommand command) {
        // 1. Create domain objects
        var productId = ProductId.generate();
        var name = new ProductName(command.name());
        var price = new Money(command.price(), command.currency());

        // 2. Call domain logic
        var product = new Product(productId, name, price, command.initialStock());

        // 3. Save
        productRepository.save(product);

        return productId;
    }

    // Use case: Reduce stock when an order is placed
    public void reduceStock(ProductId productId, int quantity) {
        var product = productRepository.findById(productId);
        product.reduceStock(quantity);  // domain logic happens here
        productRepository.save(product);
    }
}
