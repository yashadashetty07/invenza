package com.invenza.controllers;

import com.invenza.entities.Bill;
import com.invenza.services.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {
        return ResponseEntity.ok(billService.getAllBills());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(billService.getBillById(id));
    }

    @PostMapping()
    public ResponseEntity<Bill> createBill(@RequestBody Bill bill) {
        Bill savedBill = billService.createBill(bill);
        return ResponseEntity.ok(savedBill);
    }
}

