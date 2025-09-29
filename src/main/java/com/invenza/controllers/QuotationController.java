package com.invenza.controllers;

import com.invenza.dto.QuotationDTO;
import com.invenza.services.QuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotations")
public class QuotationController {

    @Autowired
    private QuotationService quotationService;

    @PostMapping
    public ResponseEntity<QuotationDTO> createQuotation(@RequestBody QuotationDTO quotationDTO) {
        return ResponseEntity.ok(quotationService.createQuotation(quotationDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuotationDTO> getQuotationById(@PathVariable Long id) {
        return ResponseEntity.ok(quotationService.getQuotationById(id));
    }

    @GetMapping
    public ResponseEntity<List<QuotationDTO>> getQuotationById() {
        return ResponseEntity.ok(quotationService.getAllQuotations());
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuotationDTO> updateQuotation(@PathVariable Long id, @RequestBody QuotationDTO dto) {
        return ResponseEntity.ok(quotationService.updateQuotation(id, dto)); // 200 OK
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuotation(@PathVariable Long id) {
        quotationService.deleteQuotation(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }


}
