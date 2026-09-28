package com.webcodein.architecture.onion.services;
import com.webcodein.architecture.onion.domain.Product;
import com.webcodein.architecture.onion.domain.ProductRepository;
import org.springframework.stereotype.Service;
@Service
public class ProductService {
    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) { this.productRepository = productRepository; }
    public Product createProduct(Product product) { return productRepository.save(product); }
}
