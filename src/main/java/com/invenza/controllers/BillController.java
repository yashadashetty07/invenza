package com.invenza.controllers;

import com.invenza.dto.BillDTO;
import com.invenza.dto.BillMapper;
import com.invenza.entities.Bill;
import com.invenza.services.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping
    public ResponseEntity<List<BillDTO>> getAllBills() {
        List<BillDTO> billDTOs = billService.getAllBills()
                .stream()
                .map(BillMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(billDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillDTO> getBillById(@PathVariable Long id) {
        Bill bill = billService.getBillById(id);
        BillDTO dto = BillMapper.toDTO(bill);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<BillDTO> createBill(@RequestBody Bill bill) {
        Bill savedBill = billService.createBill(bill);
        BillDTO dto = BillMapper.toDTO(savedBill);
        return ResponseEntity.ok(dto);
    }


}

