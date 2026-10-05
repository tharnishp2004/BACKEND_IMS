package com.example.inventory.service;

import com.example.inventory.entity.Product;
import com.example.inventory.entity.Purchase;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.repository.PurchaseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;

    public PurchaseService(
            PurchaseRepository purchaseRepository,
            ProductRepository productRepository) {

        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
    }

    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    public Purchase getPurchase(Long id) {

        return purchaseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Purchase not found"));
    }

    public Purchase createPurchase(
            Purchase purchase) {

        if (purchase.getQuantity() <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero");
        }

        Product product = productRepository
                .findById(purchase.getProductId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"));

        product.setQuantity(
                product.getQuantity() +
                purchase.getQuantity());

        productRepository.save(product);

        if (purchase.getDate() == null) {
            purchase.setDate(LocalDateTime.now(ZoneOffset.UTC));
        }

        if (purchase.getStatus() == null ||
                purchase.getStatus().isBlank()) {

            purchase.setStatus("RECEIVED");
        }

        return purchaseRepository.save(purchase);
    }

    public Purchase updatePurchase(
            Long id,
            Purchase purchase) {

        Purchase existing = getPurchase(id);

        existing.setSupplier(
                purchase.getSupplier());

        existing.setProductId(
                purchase.getProductId());

        existing.setQuantity(
                purchase.getQuantity());

        existing.setStatus(
                purchase.getStatus());

        if (purchase.getDate() != null) {
            existing.setDate(
                    purchase.getDate());
        }

        return purchaseRepository.save(existing);
    }

    public void deletePurchase(Long id) {

        if (!purchaseRepository.existsById(id)) {
            throw new RuntimeException(
                    "Purchase not found");
        }

        purchaseRepository.deleteById(id);
    }
}
