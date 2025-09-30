package com.invenza.mapper;

import com.invenza.dto.QuotationDTO;
import com.invenza.dto.QuotationItemDTO;
import com.invenza.entities.Product;
import com.invenza.entities.Quotation;
import com.invenza.entities.QuotationItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class QuotationMapper {

    // ------------------ ENTITY -> DTO ------------------
    public static QuotationDTO toDTO(Quotation quotation) {
        if (quotation == null) return null;

        QuotationDTO dto = new QuotationDTO();
        dto.setId(quotation.getId());
        dto.setCustomerName(quotation.getCustomerName());
        dto.setCustomerAddress(quotation.getCustomerAddress());
        dto.setCustomerGSTIN(quotation.getCustomerGSTIN());
        dto.setQuotationNumber(quotation.getQuotationNumber());
        dto.setCreatedAt(quotation.getCreatedAt());
        dto.setTotalAmount(quotation.getTotalAmount());

        if (quotation.getItems() != null) {
            List<QuotationItemDTO> items = quotation.getItems().stream()
                    .map(QuotationMapper::toItemDTO)
                    .collect(Collectors.toList());
            dto.setItems(items);
        }

        return dto;
    }

    private static QuotationItemDTO toItemDTO(QuotationItem item) {
        if (item == null) return null;

        QuotationItemDTO dto = new QuotationItemDTO();
        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setMrpPrice(item.getProduct().getPrice());
        }

        dto.setQuantity(item.getQuantity());
        dto.setDiscountedPrice(item.getUnitPrice());
        dto.setDiscount(item.getDiscountAmount());
        dto.setGstRate(item.getGstRate());
        dto.setTotalPrice(item.getTotalPrice());

        return dto;
    }

    // ------------------ DTO -> ENTITY ------------------
    public static Quotation toEntity(QuotationDTO dto, List<Product> products) {
        if (dto == null) throw new IllegalArgumentException("QuotationDTO is null");

        Quotation quotation = new Quotation();
        quotation.setId(dto.getId());
        quotation.setCustomerName(dto.getCustomerName());
        quotation.setCustomerAddress(dto.getCustomerAddress());
        quotation.setCustomerGSTIN(dto.getCustomerGSTIN());
        quotation.setQuotationNumber(dto.getQuotationNumber());
        quotation.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        quotation.setTotalAmount(dto.getTotalAmount());

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("QuotationDTO items are missing");
        }

        List<QuotationItem> items = dto.getItems().stream().map(itemDTO -> {
            if (itemDTO == null) throw new IllegalArgumentException("QuotationItemDTO is null");

            Product product = products.stream()
                    .filter(p -> p.getId().equals(itemDTO.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Product not found for ID " + itemDTO.getProductId()));

            QuotationItem item = new QuotationItem();
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());

            // Selling price: use frontend override if provided, else product price minus discount
            double mrp = product.getPrice();
            double sellingPrice = itemDTO.getDiscountedPrice() != null ? itemDTO.getDiscountedPrice() :
                    mrp - product.getDiscountValue();
            item.setUnitPrice(sellingPrice);

            // Discount amount
            double discountAmount = mrp - sellingPrice;
            item.setDiscountAmount(discountAmount);

            // GST %
            item.setGstRate(product.getGstRate());

            // Total Price
            double totalPrice = (sellingPrice * (1 + product.getGstRate() / 100)) * itemDTO.getQuantity();
            item.setTotalPrice(totalPrice);

            // Link back to quotation
            item.setQuotation(quotation);

            return item;
        }).collect(Collectors.toList());

        quotation.setItems(items);
        return quotation;
    }
}
