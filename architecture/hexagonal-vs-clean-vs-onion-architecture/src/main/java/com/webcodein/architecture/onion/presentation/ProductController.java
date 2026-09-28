package com.webcodein.architecture.onion.presentation;
import com.webcodein.architecture.onion.domain.Product;
import com.webcodein.architecture.onion.services.ProductService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/onion/products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) { this.productService = productService; }
    @PostMapping
    public Product createProduct(@RequestBody Product product) { return productService.createProduct(product); }
}
