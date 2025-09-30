package com.invenza.services;

import com.invenza.entities.Product;
import com.invenza.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productsRepository;

    public Product createProduct(Product product) {
        if (productsRepository.existsByHsnCode(product.getHsnCode())) {
            throw new IllegalArgumentException("HSN Code already exists: " + product.getHsnCode());
        }
        return productsRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productsRepository.findAll();
    }

    public Product getProductById(Long id) throws Exception {
        return productsRepository.findById(id).orElseThrow(() -> new Exception("Product not found with id: " + id));
    }

    public void deleteProductById(Long id) throws Exception {
        Product existing = getProductById(id);
        productsRepository.delete(existing);
    }

    public Product updateStock(Long id, double newQuantity) throws Exception {
        Product existing = getProductById(id);
        existing.setQuantity(newQuantity);
        return productsRepository.save(existing);
    }

    public Product updateProduct(Long id, Product newProduct) throws Exception {
        Product existingProduct = getProductById(id);

        existingProduct.setName(newProduct.getName());
        existingProduct.setQuantity((newProduct.getQuantity()));
        existingProduct.setPrice(newProduct.getPrice());
        existingProduct.setUnit(newProduct.getUnit());
        existingProduct.setHsnCode(newProduct.getHsnCode());
        existingProduct.setGstRate(newProduct.getGstRate());

        return productsRepository.save(existingProduct);
    }
    public List<Product> importProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("❌ Product list cannot be null or empty");
        }

        products.forEach(p -> System.out.println("➡️ Importing product: " + p));
        return productsRepository.saveAll(products);
    }


}
