package com.invenza.services;


import com.invenza.dto.QuotationDTO;
import com.invenza.dto.QuotationItemDTO;
import com.invenza.entities.Product;
import com.invenza.entities.Quotation;
import com.invenza.mapper.QuotationMapper;
import com.invenza.repositories.ProductRepository;
import com.invenza.repositories.QuotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuotationService {

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<QuotationDTO> getAllQuotations() {
        List<Quotation> quotations = quotationRepository.findAll();
        return quotations.stream()
                .map(QuotationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public QuotationDTO getQuotationById(Long id) {
        Quotation savedQuotation = quotationRepository.findQuotationById(id);
        return QuotationMapper.toDTO(savedQuotation);
    }

    public QuotationDTO createQuotation(QuotationDTO dto) {
        // 1️⃣ Fetch products
        List<Long> productIDs = dto.getItems().stream()
                .map(QuotationItemDTO::getProductId)
                .collect(Collectors.toList());

        List<Product> products = productRepository.findAllById(productIDs);

        // 2️⃣ Generate quotation number
        String quotationNumber = generateQuotationNumber();

        // 3️⃣ Map DTO to entity
        Quotation quotation = QuotationMapper.toEntity(dto, products);
        quotation.setQuotationNumber(quotationNumber);

        // 4️⃣ Calculate total amount
        double totalAmount = dto.getItems().stream()
                .mapToDouble(QuotationItemDTO::getTotalPrice)
                .sum();
        quotation.setTotalAmount(totalAmount);

        // 5️⃣ Save and return
        Quotation savedQuotation = quotationRepository.save(quotation);
        return QuotationMapper.toDTO(savedQuotation);
    }

    public QuotationDTO updateQuotation(Long id, QuotationDTO dto) {
        Quotation existingQuotation = quotationRepository.findQuotationById(id);
        if (existingQuotation == null) throw new RuntimeException("Quotation Not found");

        List<Long> productIds = dto.getItems().stream()
                .map(QuotationItemDTO::getProductId)
                .collect(Collectors.toList());

        List<Product> products = productRepository.findAllById(productIds);

        Quotation updatedQuotation = QuotationMapper.toEntity(dto, products);
        updatedQuotation.setId(existingQuotation.getId());

        double totalAmount = updatedQuotation.getItems().stream()
                .mapToDouble(item -> item.getTotalPrice())
                .sum();
        updatedQuotation.setTotalAmount(totalAmount);

        return QuotationMapper.toDTO(updatedQuotation);
    }

    public void deleteQuotation(Long id) {
        Quotation quotation = quotationRepository.findQuotationById(id);
        if (quotation == null) throw new RuntimeException("Quotation not found");
        quotationRepository.delete(quotation);
    }

    private String generateQuotationNumber() {
        long count = quotationRepository.count() + 1;
        String year = String.valueOf(LocalDateTime.now().getYear());
        return String.format("QUO-%s-%04d", year, count);
    }
}
