package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.infrastructure;

import com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain.ProductId;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(ProductId id) {
        super("Product not found with id: " + id.value());
    }
}
