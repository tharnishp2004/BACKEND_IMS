package com.example.inventory.controller;

import com.example.inventory.entity.Alert;
import com.example.inventory.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "http://localhost:3000")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public List<Alert> getAlerts() {
        return alertService.getAllAlerts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlert(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(alertService.getAlert(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Alert createAlert(@RequestBody Alert alert) {
        return alertService.createAlert(alert);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alert> updateAlert(
            @PathVariable Long id,
            @RequestBody Alert alert) {
        try {
            return ResponseEntity.ok(alertService.updateAlert(id, alert));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAlert(
            @PathVariable Long id) {
        try {
            alertService.deleteAlert(id);
            return ResponseEntity.ok("Alert deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}