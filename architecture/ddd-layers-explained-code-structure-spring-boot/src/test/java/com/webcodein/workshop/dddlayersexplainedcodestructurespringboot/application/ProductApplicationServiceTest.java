package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.application;

import com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductApplicationService service;

    @BeforeEach
    void setUp() {
        service = new ProductApplicationService(productRepository);
    }

    @Test
    @DisplayName("Should orchestrate product creation use case and persist to repository")
    void shouldCreateProduct() {
        CreateProductCommand command = new CreateProductCommand(
            "Ultra HD Monitor",
            new BigDecimal("499.99"),
            "USD",
            25
        );

        ProductId generatedId = service.createProduct(command);

        assertThat(generatedId).isNotNull();

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());

        Product savedProduct = captor.getValue();
        assertThat(savedProduct.getId()).isEqualTo(generatedId);
        assertThat(savedProduct.getName().value()).isEqualTo("Ultra HD Monitor");
        assertThat(savedProduct.getPrice().amount()).isEqualByComparingTo("499.99");
        assertThat(savedProduct.getPrice().currency()).isEqualTo("USD");
        assertThat(savedProduct.getStockQuantity()).isEqualTo(25);
    }

    @Test
    @DisplayName("Should orchestrate stock reduction use case and save updated aggregate")
    void shouldReduceStock() {
        ProductId productId = ProductId.generate();
        Product product = new Product(
            productId,
            new ProductName("Ergonomic Chair"),
            new Money(new BigDecimal("299.00"), "USD"),
            20
        );

        when(productRepository.findById(productId)).thenReturn(product);

        service.reduceStock(productId, 5);

        assertThat(product.getStockQuantity()).isEqualTo(15);
        verify(productRepository).save(product);
    }
}
