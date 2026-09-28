package com.webcodein.architecture.onion.infrastructure;
import com.webcodein.architecture.onion.domain.Product;
import com.webcodein.architecture.onion.domain.ProductRepository;
import org.springframework.stereotype.Repository;
@Repository
public class JpaProductRepository implements ProductRepository {
    @Override
    public Product save(Product product) { return product; }
}
