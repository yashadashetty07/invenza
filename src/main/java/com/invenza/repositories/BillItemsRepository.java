package com.invenza.repositories;

import com.invenza.entities.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillItemsRepository extends JpaRepository<BillItem,Long> {

    List<BillItem> findByBillId(Long billId);

    List<BillItem> findByProductId(Long productId);
}
