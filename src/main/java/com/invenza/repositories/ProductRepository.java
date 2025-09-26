package com.invenza.repositories;

import com.invenza.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Product findByName(String name);
    boolean existsByHsnCode(String hsnCode);
    Optional<Product> findByHsnCode(String hsnCode);
}
