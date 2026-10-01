package com.example.inventory.controller;

import com.example.inventory.entity.Order;
import com.example.inventory.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderService orderService;
    private final JdbcTemplate jdbcTemplate;

    public OrderController(OrderService orderService, JdbcTemplate jdbcTemplate) {
        this.orderService = orderService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<Order> getOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.getOrder(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            return ResponseEntity.ok(orderService.createOrder(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(
            @PathVariable Long id,
            @RequestBody Order order) {
        try {
            return ResponseEntity.ok(orderService.updateOrder(id, order));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);

            // Reset AUTO_INCREMENT so next order gets a clean sequential ID
            try {
                Long maxId = jdbcTemplate.queryForObject(
                        "SELECT COALESCE(MAX(id), 0) FROM orders", Long.class);
                jdbcTemplate.execute("ALTER TABLE orders AUTO_INCREMENT = " + (maxId + 1));
            } catch (Exception ignored) {}

            return ResponseEntity.ok("Order deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}