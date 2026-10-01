package com.example.inventory.service;

import com.example.inventory.entity.Product;
import com.example.inventory.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"));
    }

    public Product addProduct(Product product) {

        return productRepository.save(product);
    }

    public Product updateProduct(
            Long id,
            Product product) {

        Product existing = getProduct(id);

        existing.setName(product.getName());
        existing.setSku(product.getSku());
        existing.setCategory(product.getCategory());
        existing.setPrice(product.getPrice());
        existing.setQuantity(product.getQuantity());
        existing.setSupplier(product.getSupplier());
        existing.setExpiryDate(product.getExpiryDate());

        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {

        if (!productRepository.existsById(id)) {
            throw new RuntimeException(
                    "Product not found");
        }

        productRepository.deleteById(id);
    }

    public List<Product> searchByName(String name) {

        return productRepository
                .findByNameContainingIgnoreCase(name);
    }

    public List<Product> searchByCategory(String category) {

        return productRepository
                .findByCategoryContainingIgnoreCase(category);
    }

    public List<Product> searchBySupplier(String supplier) {

        return productRepository
                .findBySupplierContainingIgnoreCase(supplier);
    }

    public List<Product> getLowStock(int quantity) {

        return productRepository
                .findByQuantityLessThan(quantity);
    }
}