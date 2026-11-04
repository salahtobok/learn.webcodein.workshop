package com.webcodein.boundedcontext.catalog;

import com.webcodein.boundedcontext.catalog.dto.CreateProductRequest;
import com.webcodein.boundedcontext.catalog.dto.ProductResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CatalogService {

    private final ProductRepository productRepository;

    public CatalogService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDto createProduct(CreateProductRequest request) {
        Product product = new Product(
            request.sku(),
            request.name(),
            request.description(),
            request.price()
        );
        Product saved = productRepository.save(product);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Optional<ProductResponseDto> getProductById(UUID id) {
        return productRepository.findById(id).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ProductResponseDto> getProductBySku(String sku) {
        return productRepository.findBySku(sku).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
            .map(this::toDto)
            .toList();
    }

    private ProductResponseDto toDto(Product p) {
        return new ProductResponseDto(
            p.getId(),
            p.getSku(),
            p.getName(),
            p.getDescription(),
            p.getPrice()
        );
    }
}
