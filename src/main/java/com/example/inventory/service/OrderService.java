package com.example.inventory.service;

import com.example.inventory.entity.Order;
import com.example.inventory.entity.Product;
import com.example.inventory.repository.OrderRepository;
import com.example.inventory.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrder(Long id) {

        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"));
    }

    public Order createOrder(Order order) {

        if (order.getQuantity() <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero");
        }

        Product product = productRepository
                .findById(order.getProductId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"));

        if (product.getQuantity() < order.getQuantity()) {
            throw new RuntimeException(
                    "Insufficient stock");
        }

        product.setQuantity(
                product.getQuantity() -
                order.getQuantity());

        productRepository.save(product);

        if (order.getDate() == null) {
            order.setDate(LocalDateTime.now(ZoneOffset.UTC));
        }

        if (order.getOrderStatus() == null ||
                order.getOrderStatus().isBlank()) {

            order.setOrderStatus("PENDING");
        }

        if (order.getTotalPrice() == 0) {

            order.setTotalPrice(
                    product.getPrice() *
                    order.getQuantity());
        }

        return orderRepository.save(order);
    }

    public Order updateOrder(
            Long id,
            Order order) {

        Order existing = getOrder(id);

        existing.setProductId(order.getProductId());
        existing.setQuantity(order.getQuantity());
        existing.setTotalPrice(order.getTotalPrice());
        existing.setOrderStatus(order.getOrderStatus());

        if (order.getDate() != null) {
            existing.setDate(order.getDate());
        }

        return orderRepository.save(existing);
    }

    public void deleteOrder(Long id) {

        if (!orderRepository.existsById(id)) {
            throw new RuntimeException(
                    "Order not found");
        }

        orderRepository.deleteById(id);
    }
}
