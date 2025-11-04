package com.invenza.controllers;

import com.invenza.dto.BillDTO;
import com.invenza.mapper.BillMapper;
import com.invenza.dto.QuotationDTO;
import com.invenza.services.BillService;
import com.invenza.services.PDFService;
import com.invenza.services.QuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pdf")
public class PDFController {

    @Autowired
    private PDFService pdfService;

    @Autowired
    private QuotationService quotationService;

    @Autowired
    private BillService billService;

    public PDFController(PDFService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/bills/{id}")
    public ResponseEntity<byte[]> generateBill(@PathVariable Long id) throws Exception {
        // Fetch entity from DB
        var billEntity = billService.getBillById(id);

        // Convert to DTO using mapper
        BillDTO billDTO = BillMapper.toDTO(billEntity);

        // Generate PDF
        byte[] pdf = pdfService.generateBillPDF(billDTO);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=bill-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/quotations/{id}")
    public ResponseEntity<byte[]> generateQuotation(@PathVariable Long id) throws Exception {
        QuotationDTO quotation = quotationService.getQuotationById(id);
        byte[] pdf = pdfService.generateQuotationPDF(quotation);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=quotation-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
