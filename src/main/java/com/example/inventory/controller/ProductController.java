package com.example.inventory.controller;

import com.example.inventory.entity.Product;
import com.example.inventory.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private final ProductService productService;
    private final JdbcTemplate jdbcTemplate;

    public ProductController(ProductService productService, JdbcTemplate jdbcTemplate) {
        this.productService = productService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        return productService.updateProduct(id, product);
    }

    @PatchMapping("/{id}")
    public Product patchProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        Product existing = productService.getProduct(id);
        if (product.getName() != null) existing.setName(product.getName());
        if (product.getSku() != null) existing.setSku(product.getSku());
        if (product.getCategory() != null) existing.setCategory(product.getCategory());
        if (product.getPrice() > 0) existing.setPrice(product.getPrice());
        if (product.getQuantity() >= 0) existing.setQuantity(product.getQuantity());
        if (product.getSupplier() != null) existing.setSupplier(product.getSupplier());
        if (product.getExpiryDate() != null) existing.setExpiryDate(product.getExpiryDate());

        return productService.addProduct(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        // Reset AUTO_INCREMENT so next product gets a clean sequential ID
        try {
            Long maxId = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(MAX(id), 0) FROM products", Long.class);
            jdbcTemplate.execute("ALTER TABLE products AUTO_INCREMENT = " + (maxId + 1));
        } catch (Exception ignored) {}

        return ResponseEntity.ok("Product deleted successfully");
    }

    @GetMapping("/search/name")
    public List<Product> searchName(
            @RequestParam String name) {

        return productService.searchByName(name);
    }

    @GetMapping("/search/category")
    public List<Product> searchCategory(
            @RequestParam String category) {

        return productService.searchByCategory(category);
    }

    @GetMapping("/search/supplier")
    public List<Product> searchSupplier(
            @RequestParam String supplier) {

        return productService.searchBySupplier(supplier);
    }

    @GetMapping("/low-stock")
    public List<Product> lowStock(
            @RequestParam(defaultValue = "10") int quantity) {

        return productService.getLowStock(quantity);
    }
}