package com.invenza.services;


import com.invenza.dto.QuotationDTO;
import com.invenza.dto.QuotationItemDTO;
import com.invenza.entities.Product;
import com.invenza.entities.Quotation;
import com.invenza.entities.QuotationItem;
import com.invenza.mapper.QuotationMapper;
import com.invenza.repositories.ProductRepository;
import com.invenza.repositories.QuotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
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
        if (dto == null) {
            throw new IllegalArgumentException("QuotationDTO is null");
        }

        // 1️⃣ Fetch products safely
        List<Long> productIDs = dto.getItems() != null
                ? dto.getItems().stream().map(QuotationItemDTO::getProductId).collect(Collectors.toList())
                : List.of();

        if (productIDs.isEmpty()) {
            throw new IllegalArgumentException("Quotation must have at least one product");
        }

        List<Product> products = productRepository.findAllById(productIDs);
        if (products.isEmpty()) {
            throw new RuntimeException("No valid products found for given IDs");
        }

        // 2️⃣ Generate quotation number
        String quotationNumber = generateQuotationNumber();

        // 3️⃣ Map DTO → Entity
        Quotation quotation = QuotationMapper.toEntity(dto, products);

        // Ensure number and createdAt set AFTER mapping
        quotation.setQuotationNumber(quotationNumber);
        quotation.setCreatedAt(LocalDateTime.now());

        // 4️⃣ Compute total amount from items
        double totalAmount = quotation.getItems().stream()
                .mapToDouble(QuotationItem::getTotalPrice)
                .sum();
        quotation.setTotalAmount(totalAmount);

        // 5️⃣ Persist and return
        Quotation savedQuotation = quotationRepository.save(quotation);
        return QuotationMapper.toDTO(savedQuotation);
    }

    public QuotationDTO updateQuotation(Long id, QuotationDTO dto) {
        // 🔹 1. Fetch existing quotation safely
        Quotation existingQuotation = quotationRepository.findQuotationById(id);
        if (existingQuotation == null) {
            throw new RuntimeException("Quotation not found with id: " + id);
        }

        // 🔹 2. Fetch related products
        List<Long> productIds = dto.getItems().stream()
                .map(QuotationItemDTO::getProductId)
                .collect(Collectors.toList());
        List<Product> products = productRepository.findAllById(productIds);

        // 🔹 3. Map DTO → new entity
        Quotation updatedQuotation = QuotationMapper.toEntity(dto, products);

        // 🔹 4. Preserve immutable fields
        updatedQuotation.setId(existingQuotation.getId());
        updatedQuotation.setQuotationNumber(existingQuotation.getQuotationNumber());
        updatedQuotation.setCreatedAt(existingQuotation.getCreatedAt());

        // 🔹 5. Set updated timestamp
        updatedQuotation.setUpdatedAt(LocalDateTime.now());

        // 🔹 6. Recalculate total
        double totalAmount = updatedQuotation.getItems().stream()
                .mapToDouble(QuotationItem::getTotalPrice)
                .sum();
        updatedQuotation.setTotalAmount(totalAmount);

        // 🔹 7. Save
        Quotation savedQuotation = quotationRepository.save(updatedQuotation);

        return QuotationMapper.toDTO(savedQuotation);
    }


    public void deleteQuotation(Long id) {
        Quotation quotation = quotationRepository.findQuotationById(id);
        if (quotation == null) throw new RuntimeException("Quotation not found");
        quotationRepository.delete(quotation);
    }

    public String generateQuotationNumber() {
            String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
            long countToday = quotationRepository.count(); // total bills (you can refine by date if needed)
            String sequence = new DecimalFormat("0000").format(countToday + 1);
            return "QTN-" + datePart + "-" + sequence;
        }
}
