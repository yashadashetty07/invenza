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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBill(@PathVariable Long id){
        billService.deleteBill(id);
    return ResponseEntity.ok("ok");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillDTO> updateBill(@PathVariable Long id, @RequestBody BillDTO billDTO) {
        Bill existingBill = billService.getBillById(id);
        if (existingBill == null) {
            return ResponseEntity.notFound().build();
        }

        // Update simple fields
        existingBill.setCustomerName(billDTO.getCustomerName());
        existingBill.setBillDate(billDTO.getBillDate());
        existingBill.setCustomerAddress(billDTO.getCustomerAddress());
        existingBill.setCustomerGSTIN(billDTO.getCustomerGSTIN());

        // Replace existing items with updated ones
        Bill updatedData = BillMapper.fromDTO(billDTO, productRepository);
        existingBill.getItems().clear();
        existingBill.getItems().addAll(updatedData.getItems());

        // Recalculate and save (reuse your createBill logic)
        Bill updatedBill = billService.updateBill(existingBill);
        return ResponseEntity.ok(BillMapper.toDTO(updatedBill));
    }


}


