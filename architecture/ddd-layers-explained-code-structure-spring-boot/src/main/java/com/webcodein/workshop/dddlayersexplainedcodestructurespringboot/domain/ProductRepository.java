package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain;

import java.util.List;

public interface ProductRepository {
    Product findById(ProductId id);
    void save(Product product);
    List<Product> findByPriceBelow(Money maxPrice);
}
