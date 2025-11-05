package com.invenza.services;


import com.invenza.dto.QuotationDTO;
import com.invenza.entities.Product;
import com.invenza.entities.Quotation;
import com.invenza.entities.Vendor;
import com.invenza.mapper.QuotationMapper;
import com.invenza.repositories.ProductRepository;
import com.invenza.repositories.QuotationRepository;
import com.invenza.repositories.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuotationService {

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<QuotationDTO> getAllQuotations() {
        List<Quotation> quotations = quotationRepository.findAll();
        List<QuotationDTO> quotationDTOs = quotations.stream().map(QuotationMapper::toDTO).collect(Collectors.toList());
        return quotationDTOs;
    }

    public QuotationDTO getQuotationById(Long id) {
        Quotation savedQuotation = quotationRepository.findQuotationById(id);
        return QuotationMapper.toDTO(savedQuotation);
    }

    public QuotationDTO createQuotation(QuotationDTO dto) {
        //fetch vendor
        Vendor vendor = vendorRepository.findByid(dto.getVendorId());
        if (vendor == null) {
            throw new RuntimeException("Vendor not found");
        }
        //fetch products
        List<Long> productIDs = dto.getItems().stream().map(map -> map.getProductId()).collect(Collectors.toList());

        List<Product> products = productRepository.findAllById(productIDs);

        //map dto to entity
        Quotation quotation = QuotationMapper.toEntity(dto, vendor, products);
        double totalAmount = dto.getItems().stream().mapToDouble(item -> item.getTotalPrice()).sum();

        quotation.setTotalAmount(totalAmount);

        Quotation savedQuotations = quotationRepository.save(quotation);

        return QuotationMapper.toDTO(savedQuotations);
    }

    public QuotationDTO updateQuotation(Long id, QuotationDTO quotationDTO) {
        Quotation existingQuotation = quotationRepository.findQuotationById(id);
        if (existingQuotation == null) throw new RuntimeException("Quotation Not found");

        Vendor vendor = vendorRepository.findByid(quotationDTO.getVendorId());
        if (vendor == null) throw new RuntimeException("Vendor not found");
        List<Long> productIds = quotationDTO.getItems()
                .stream()
                .map(i -> i.getProductId())
                .collect(Collectors.toList());

        List<Product> products = productRepository.findAllById(productIds);

        Quotation updatedQuotation = QuotationMapper.toEntity(quotationDTO, vendor, products);
        updatedQuotation.setId(existingQuotation.getId());

        double totalAmount = updatedQuotation.getItems().stream().mapToDouble(item -> item.getTotalPrice()).sum();
        updatedQuotation.setTotalAmount(totalAmount);

        return QuotationMapper.toDTO(updatedQuotation);
    }

    public void deleteQuotation(Long id) {
        Quotation quotation = quotationRepository.findQuotationById(id);
        if (quotation == null) throw new RuntimeException("Quotation not found");
        quotationRepository.delete(quotation);
    }

}

