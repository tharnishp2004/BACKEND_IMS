package com.example.inventory.service;

import com.example.inventory.entity.Alert;
import com.example.inventory.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public Alert getAlert(Long id) {

        return alertRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Alert not found"));
    }

    public Alert createAlert(Alert alert) {

        if (alert.getStatus() == null ||
                alert.getStatus().isBlank()) {

            alert.setStatus("ACTIVE");
        }

        return alertRepository.save(alert);
    }

    public Alert updateAlert(
            Long id,
            Alert alert) {

        Alert existing = getAlert(id);

        existing.setType(alert.getType());
        existing.setMessage(alert.getMessage());
        existing.setStatus(alert.getStatus());

        return alertRepository.save(existing);
    }

    public void deleteAlert(Long id) {

        if (!alertRepository.existsById(id)) {
            throw new RuntimeException(
                    "Alert not found");
        }

        alertRepository.deleteById(id);
    }
}