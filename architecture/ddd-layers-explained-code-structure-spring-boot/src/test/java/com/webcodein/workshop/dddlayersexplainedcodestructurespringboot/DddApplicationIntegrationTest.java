package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot;

import com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.application.CreateProductCommand;
import com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.application.ProductApplicationService;
import com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain.Product;
import com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain.ProductId;
import com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DddApplicationIntegrationTest {

    @Autowired
    private ProductApplicationService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Should execute complete DDD lifecycle across all 4 layers")
    void shouldExecuteCompleteDddLifecycle() {
        // Presentation / Client initiates Command into Application Layer
        CreateProductCommand command = new CreateProductCommand(
            "Mechanical Keyboard Pro",
            new BigDecimal("189.95"),
            "USD",
            10
        );

        ProductId productId = productService.createProduct(command);
        assertThat(productId).isNotNull();

        // Verify retrieval and reconstitution from Infrastructure JPA Repository
        Product retrieved = productRepository.findById(productId);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName().value()).isEqualTo("Mechanical Keyboard Pro");
        assertThat(retrieved.getPrice().amount()).isEqualByComparingTo("189.95");
        assertThat(retrieved.getStockQuantity()).isEqualTo(10);
        assertThat(retrieved.isInStock()).isTrue();

        // Exercise business invariant mutation through Application Service
        productService.reduceStock(productId, 3);

        Product updated = productRepository.findById(productId);
        assertThat(updated.getStockQuantity()).isEqualTo(7);
    }
}
