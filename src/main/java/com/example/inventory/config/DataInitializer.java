package com.example.inventory.config;

import com.example.inventory.entity.*;
import com.example.inventory.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PurchaseRepository purchaseRepository;
    private final AlertRepository alertRepository;
    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(
            UserRepository userRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            PurchaseRepository purchaseRepository,
            AlertRepository alertRepository,
            JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.purchaseRepository = purchaseRepository;
        this.alertRepository = alertRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            jdbcTemplate.execute("ALTER TABLE products MODIFY id BIGINT NOT NULL AUTO_INCREMENT FIRST, MODIFY sku VARCHAR(255) AFTER id, MODIFY name VARCHAR(255) AFTER sku, MODIFY category VARCHAR(255) AFTER name, MODIFY price FLOAT(53) AFTER category, MODIFY quantity INT AFTER price, MODIFY supplier VARCHAR(255) AFTER quantity, MODIFY expiry_date DATE AFTER supplier;");
            jdbcTemplate.execute("ALTER TABLE orders MODIFY id BIGINT NOT NULL AUTO_INCREMENT FIRST, MODIFY product_id BIGINT AFTER id, MODIFY quantity INT AFTER product_id, MODIFY total_price FLOAT(53) AFTER quantity, MODIFY order_status VARCHAR(255) AFTER total_price, MODIFY date DATETIME(6) AFTER order_status;");
            jdbcTemplate.execute("ALTER TABLE purchases MODIFY id BIGINT NOT NULL AUTO_INCREMENT FIRST, MODIFY supplier VARCHAR(255) AFTER id, MODIFY product_id BIGINT AFTER supplier, MODIFY quantity INT AFTER product_id, MODIFY status VARCHAR(255) AFTER quantity, MODIFY date DATETIME(6) AFTER status;");
            jdbcTemplate.execute("ALTER TABLE users MODIFY id BIGINT NOT NULL AUTO_INCREMENT FIRST, MODIFY name VARCHAR(255) AFTER id, MODIFY username VARCHAR(255) AFTER name, MODIFY password VARCHAR(255) AFTER username, MODIFY role VARCHAR(255) AFTER password;");
            jdbcTemplate.execute("ALTER TABLE alerts MODIFY id BIGINT NOT NULL AUTO_INCREMENT FIRST, MODIFY type VARCHAR(255) AFTER id, MODIFY message VARCHAR(255) AFTER type, MODIFY status VARCHAR(255) AFTER message;");
        } catch (Exception e) {
            // Ignore if columns already reordered
        }

        // Reset AUTO_INCREMENT to 1 for all tables if they're empty
        try {
            if (userRepository.count() == 0) jdbcTemplate.execute("ALTER TABLE users AUTO_INCREMENT = 1;");
            if (productRepository.count() == 0) jdbcTemplate.execute("ALTER TABLE products AUTO_INCREMENT = 1;");
            if (orderRepository.count() == 0) jdbcTemplate.execute("ALTER TABLE orders AUTO_INCREMENT = 1;");
            if (purchaseRepository.count() == 0) jdbcTemplate.execute("ALTER TABLE purchases AUTO_INCREMENT = 1;");
            if (alertRepository.count() == 0) jdbcTemplate.execute("ALTER TABLE alerts AUTO_INCREMENT = 1;");
        } catch (Exception e) {
            // Ignore
        }

        // No default users are seeded.
        // All new accounts register as VIEWER (Guest Viewer).
        // Roles can be changed later via Admin → Users panel.

        // Seed Products if empty
        if (productRepository.count() == 0) {
            Product p1 = new Product("Wireless Mechanical Keyboard", "KB-WL-01", "Electronics", 89.99, 25, "TechLogistics", LocalDate.of(2027, 12, 31));
            Product p2 = new Product("Ergonomic Office Chair", "CH-ERG-02", "Furniture", 249.50, 8, "ComfortSupply", LocalDate.of(2030, 1, 1));
            Product p3 = new Product("Ultra HD 4K Monitor 27-inch", "MON-4K-27", "Electronics", 320.00, 3, "ScreenCorp", LocalDate.of(2028, 6, 15));
            Product p4 = new Product("USB-C Fast Charging Cable", "CBL-USBC-04", "Accessories", 12.99, 0, "CableWorks", LocalDate.of(2029, 1, 1));

            p1 = productRepository.save(p1);
            p2 = productRepository.save(p2);
            p3 = productRepository.save(p3);
            p4 = productRepository.save(p4);

            // Seed Orders
            if (orderRepository.count() == 0) {
                Order o1 = new Order(p1.getId(), 2, 179.98, "COMPLETED", LocalDateTime.of(2026, 9, 20, 14, 30));
                Order o2 = new Order(p2.getId(), 1, 249.50, "COMPLETED", LocalDateTime.of(2026, 9, 22, 16, 45));
                orderRepository.save(o1);
                orderRepository.save(o2);
            }

            // Seed Purchases
            if (purchaseRepository.count() == 0) {
                Purchase pur1 = new Purchase("ScreenCorp", p3.getId(), 10, "PENDING", LocalDateTime.of(2026, 9, 24, 9, 15));
                purchaseRepository.save(pur1);
            }

        }

        // Create persisted alerts from the current inventory, even when products
        // already existed before this application started.
        if (alertRepository.count() == 0) {
            List<Alert> initialAlerts = new ArrayList<>();

            for (Product product : productRepository.findAll()) {
                if (product.getQuantity() == 0) {
                    initialAlerts.add(new Alert(
                            "OUT_OF_STOCK",
                            product.getName() + " is completely out of stock.",
                            "CRITICAL"));
                } else if (product.getQuantity() <= 5) {
                    initialAlerts.add(new Alert(
                            "LOW_STOCK",
                            product.getName() + " is down to " + product.getQuantity() + " units.",
                            "ACTIVE"));
                }
            }

            for (Purchase purchase : purchaseRepository.findAll()) {
                if ("PENDING".equalsIgnoreCase(purchase.getStatus())) {
                    String productName = purchase.getProductId() == null
                            ? "Product"
                            : productRepository.findById(purchase.getProductId())
                                    .map(Product::getName)
                                    .orElse("Product");
                    initialAlerts.add(new Alert(
                            "ORDER",
                            "Order #" + purchase.getId() + " for " + productName
                                    + " from " + purchase.getSupplier() + " is pending.",
                            "ACTIVE"));
                }
            }

            alertRepository.saveAll(initialAlerts);
        }
    }
}
