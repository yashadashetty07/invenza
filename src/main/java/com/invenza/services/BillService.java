package com.invenza.services;

import com.invenza.entities.Bill;
import com.invenza.entities.BillItem;
import com.invenza.repositories.BillItemsRepository;
import com.invenza.repositories.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillItemsRepository billItemsRepository;

    public Bill createBill(Bill bill) {
        for (BillItem item : bill.getItems()) {
            item.setBill(bill);
        }
        return billRepository.save(bill);
    }


    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Bill getBillById(Long id) {
        return billRepository.findBillById(id);
    }
}
