package com.invenza.repositories;

import com.invenza.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByName(String name);

    boolean existsByHsnCode(String hsnCode);

    Optional<Product> findByHsnCode(String hsnCode);

    @Query("SELECT p.hsnCode FROM Product p")
    List<String> findAllHsns();

}
