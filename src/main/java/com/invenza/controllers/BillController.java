package com.invenza.controllers;

import com.invenza.dto.BillDTO;
import com.invenza.entities.Bill;
import com.invenza.mapper.BillMapper;
import com.invenza.repositories.ProductRepository;
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

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<BillDTO>> getAllBills() {
        List<BillDTO> billDTOs = billService.getAllBills().stream().map(BillMapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(billDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillDTO> getBillById(@PathVariable Long id) {
        Bill bill = billService.getBillById(id); // never null anymore
        BillDTO dto = BillMapper.toDTO(bill);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<BillDTO> createBill(@RequestBody BillDTO billDTO) {
        System.out.println("🔧 Received BillDTO: billNumber=" + billDTO.getBillNumber() + ", customer=" + billDTO.getCustomerName() + ", itemsCount=" + (billDTO.getItems() == null ? 0 : billDTO.getItems().size()));

        Bill bill = BillMapper.fromDTO(billDTO, productRepository);
        System.out.println("🛠️ Mapped Bill entity: billNumber=" + bill.getBillNumber() + ", itemsCount=" + (bill.getItems() == null ? 0 : bill.getItems().size()));

        Bill savedBill = billService.createBill(bill);
        System.out.println("💾 Saved Bill: id=" + savedBill.getId() + ", billNumber=" + savedBill.getBillNumber() + ", finalAmount=" + savedBill.getFinalAmount());

        BillDTO dto = BillMapper.toDTO(savedBill);
        System.out.println("📤 Returning BillDTO: id=" + dto.getId() + ", billNumber=" + dto.getBillNumber());

        return ResponseEntity.ok(dto);
    }

}


