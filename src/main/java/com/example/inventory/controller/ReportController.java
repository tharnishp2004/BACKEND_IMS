package com.example.inventory.controller;

import com.example.inventory.entity.Order;
import com.example.inventory.entity.Product;
import com.example.inventory.repository.OrderRepository;
import com.example.inventory.repository.ProductRepository;
import com.example.inventory.repository.PurchaseRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:3000")
public class ReportController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PurchaseRepository purchaseRepository;

    public ReportController(
            ProductRepository productRepository,
            OrderRepository orderRepository,
            PurchaseRepository purchaseRepository) {

        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.purchaseRepository = purchaseRepository;
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {

        Map<String, Object> report = new HashMap<>();

        report.put(
                "totalProducts",
                productRepository.count()
        );

        report.put(
                "totalOrders",
                orderRepository.count()
        );

        report.put(
                "totalPurchases",
                purchaseRepository.count()
        );

        report.put(
                "lowStockProducts",
                productRepository.findByQuantityLessThan(10).size()
        );

        return report;
    }

    @GetMapping("/sales/csv")
    public ResponseEntity<byte[]> downloadSalesCsv() {
        List<Order> orders = orderRepository.findAll();
        Map<Long, String> productMap = productRepository.findAll().stream()
                .collect(Collectors.toMap(Product::getId, Product::getName, (a, b) -> a));

        StringBuilder sb = new StringBuilder();
        // UTF-8 BOM for Excel compatibility
        sb.append("\uFEFF");
        sb.append("Order ID,Product Name,Quantity,Total Price,Order Status,Date\n");

        for (Order o : orders) {
            String prodName = productMap.getOrDefault(o.getProductId(), "Product #" + o.getProductId());
            sb.append("\"").append(o.getId()).append("\",");
            sb.append("\"").append(prodName.replace("\"", "\"\"")).append("\",");
            sb.append(o.getQuantity()).append(",");
            sb.append(o.getTotalPrice()).append(",");
            sb.append("\"").append(o.getOrderStatus() != null ? o.getOrderStatus() : "COMPLETED").append("\",");
            sb.append("\"").append(o.getDate() != null ? o.getDate().toString() : "").append("\"\n");
        }

        byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sales_report_" + LocalDate.now() + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvBytes);
    }

    @GetMapping("/inventory/csv")
    public ResponseEntity<byte[]> downloadInventoryCsv() {
        List<Product> products = productRepository.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("\uFEFF");
        sb.append("Product ID,SKU,Name,Category,Unit Price,Quantity,Total Valuation,Supplier,Expiry Date\n");

        for (Product p : products) {
            double totalVal = p.getPrice() * p.getQuantity();
            sb.append("\"").append(p.getId()).append("\",");
            sb.append("\"").append(p.getSku() != null ? p.getSku() : "").append("\",");
            sb.append("\"").append(p.getName() != null ? p.getName().replace("\"", "\"\"") : "").append("\",");
            sb.append("\"").append(p.getCategory() != null ? p.getCategory().replace("\"", "\"\"") : "").append("\",");
            sb.append(p.getPrice()).append(",");
            sb.append(p.getQuantity()).append(",");
            sb.append(String.format("%.2f", totalVal)).append(",");
            sb.append("\"").append(p.getSupplier() != null ? p.getSupplier().replace("\"", "\"\"") : "").append("\",");
            sb.append("\"").append(p.getExpiryDate() != null ? p.getExpiryDate().toString() : "").append("\"\n");
        }

        byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"inventory_report_" + LocalDate.now() + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvBytes);
    }

    @GetMapping("/summary/csv")
    public ResponseEntity<byte[]> downloadSummaryCsv() {
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        long totalPurchases = purchaseRepository.count();
        int lowStock = productRepository.findByQuantityLessThan(10).size();

        StringBuilder sb = new StringBuilder();
        sb.append("\uFEFF");
        sb.append("Metric,Value\n");
        sb.append("Total Products,").append(totalProducts).append("\n");
        sb.append("Total Orders,").append(totalOrders).append("\n");
        sb.append("Total Purchases,").append(totalPurchases).append("\n");
        sb.append("Low Stock Products,").append(lowStock).append("\n");

        byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"summary_report_" + LocalDate.now() + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvBytes);
    }
}