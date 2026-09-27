package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.presentation;

import com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.application.ProductApplicationService;
import com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.application.CreateProductCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductApplicationService productService;

    public ProductController(ProductApplicationService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        // Map request to command
        var command = new CreateProductCommand(
            request.name(), request.price(), request.currency(), request.initialStock()
        );

        // Delegate to application layer
        var productId = productService.createProduct(command);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ProductResponse(productId.value().toString(), "Product created"));
    }
}
