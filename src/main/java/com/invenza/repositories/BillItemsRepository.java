package com.invenza.repositories;

import com.invenza.entities.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillItemsRepository extends JpaRepository<BillItem,Long> {

    List<BillItem> findByBillId(Long billId);

    List<BillItem> findByProductId(Long productId);
}
