package com.invenza.repositories;

import com.invenza.entities.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<Vendor,Long> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByGstNumber(String gstNumber);

    Vendor findByid(Long id);
}
