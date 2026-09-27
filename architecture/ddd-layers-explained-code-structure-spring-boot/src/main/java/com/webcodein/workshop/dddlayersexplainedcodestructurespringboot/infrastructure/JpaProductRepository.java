package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.infrastructure;

import com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

// JPA Repository implementation — implements the Domain interface
@Repository
public class JpaProductRepository implements ProductRepository {

    private final SpringDataProductRepository springRepo;

    public JpaProductRepository(SpringDataProductRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public Product findById(ProductId id) {
        var entity = springRepo.findById(id.value())
            .orElseThrow(() -> new ProductNotFoundException(id));
        return toDomain(entity);  // map JPA entity to domain object
    }

    @Override
    public void save(Product product) {
        var entity = toJpaEntity(product);  // map domain object to JPA entity
        springRepo.save(entity);
    }

    @Override
    public List<Product> findByPriceBelow(Money maxPrice) {
        // Implementation omitted for brevity
        return List.of();
    }

    // Mapping methods
    private Product toDomain(ProductJpaEntity entity) {
        return new Product(
            new ProductId(entity.getId()),
            new ProductName(entity.getName()),
            new Money(entity.getPrice(), entity.getCurrency()),
            entity.getStockQuantity()
        );
    }

    private ProductJpaEntity toJpaEntity(Product product) {
        return new ProductJpaEntity(
            product.getId().value(),
            product.getName().value(),
            product.getPrice().amount(),
            product.getPrice().currency(),
            product.getStockQuantity()
        );
    }
}
