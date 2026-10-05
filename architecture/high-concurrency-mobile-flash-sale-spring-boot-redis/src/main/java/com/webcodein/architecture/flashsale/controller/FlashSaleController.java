package com.webcodein.architecture.flashsale.controller;
import com.webcodein.architecture.flashsale.service.FlashSaleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flash-sale")
public class FlashSaleController {
    private final FlashSaleService flashSaleService;

    public FlashSaleController(FlashSaleService flashSaleService) {
        this.flashSaleService = flashSaleService;
    }

    @PostMapping("/{productId}/purchase")
    public ResponseEntity<String> purchase(@PathVariable String productId, @RequestParam String userId) {
        boolean success = flashSaleService.purchase(productId, userId);
        if (success) {
            return ResponseEntity.ok("Purchase successful! Order is being processed.");
        } else {
            return ResponseEntity.badRequest().body("Sold out!");
        }
    }
}
