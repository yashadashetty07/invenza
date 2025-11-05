package com.invenza.repositories;

import com.invenza.entities.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    Quotation findQuotationById(Long id);
}
