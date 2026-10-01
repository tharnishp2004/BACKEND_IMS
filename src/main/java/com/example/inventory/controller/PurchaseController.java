package com.example.inventory.controller;

import com.example.inventory.entity.Purchase;
import com.example.inventory.service.PurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "http://localhost:3000")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final JdbcTemplate jdbcTemplate;

    public PurchaseController(PurchaseService purchaseService, JdbcTemplate jdbcTemplate) {
        this.purchaseService = purchaseService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<Purchase> getPurchases() {
        return purchaseService.getAllPurchases();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Purchase> getPurchase(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(purchaseService.getPurchase(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Purchase> createPurchase(
            @RequestBody Purchase purchase) {
        try {
            return ResponseEntity.ok(purchaseService.createPurchase(purchase));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Purchase> updatePurchase(
            @PathVariable Long id,
            @RequestBody Purchase purchase) {
        try {
            return ResponseEntity.ok(purchaseService.updatePurchase(id, purchase));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePurchase(@PathVariable Long id) {
        try {
            purchaseService.deletePurchase(id);

            // Reset AUTO_INCREMENT so next purchase gets a clean sequential ID
            try {
                Long maxId = jdbcTemplate.queryForObject(
                        "SELECT COALESCE(MAX(id), 0) FROM purchases", Long.class);
                jdbcTemplate.execute("ALTER TABLE purchases AUTO_INCREMENT = " + (maxId + 1));
            } catch (Exception ignored) {}

            return ResponseEntity.ok("Purchase deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}