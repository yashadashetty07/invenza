package com.invenza.mapper;

import com.invenza.dto.QuotationDTO;
import com.invenza.dto.QuotationItemDTO;
import com.invenza.entities.Product;
import com.invenza.entities.Quotation;
import com.invenza.entities.QuotationItem;

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
            dto.setHsnCode(item.getProduct().getHsnCode());
        }

        double quantity = (item.getQuantity() != null) ? item.getQuantity() : 0.0;
        double mrp = (item.getProduct() != null && (Double) item.getProduct().getPrice() != null)
                ? item.getProduct().getPrice()
                : 0.0;
        double discounted = (item.getUnitPrice() != null) ? item.getUnitPrice() : mrp;
        double gstRate = (item.getGstRate() != null) ? item.getGstRate() : 0.0;

        // Calculate total price safely
        double baseTotal = discounted * quantity;
        double gstAmount = baseTotal * gstRate / 100;
        double total = baseTotal + gstAmount;

        dto.setQuantity(quantity);
        dto.setDiscountedPrice(discounted);
        dto.setGstRate(gstRate);
        dto.setTotalPrice(total);

        // Compute discount percentage properly
        double discountPercent = (mrp > 0) ? ((mrp - discounted) / mrp) * 100 : 0.0;
        dto.setDiscount(discountPercent);

        return dto;
    }

    // ------------------ DTO -> ENTITY ------------------
    public static Quotation toEntity(QuotationDTO dto, List<Product> products) {
        if (dto == null) {
            throw new IllegalArgumentException("QuotationDTO is null");
        }

        Quotation quotation = new Quotation();
        quotation.setId(dto.getId());
        quotation.setCustomerName(dto.getCustomerName());
        quotation.setCustomerAddress(dto.getCustomerAddress());
        quotation.setCustomerGSTIN(dto.getCustomerGSTIN());
        quotation.setTotalAmount(dto.getTotalAmount());

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("QuotationDTO items are missing");
        }

        List<QuotationItem> items = dto.getItems().stream().map(itemDTO -> {
            if (itemDTO == null) {
                throw new IllegalArgumentException("QuotationItemDTO is null");
            }

            Product product = products.stream()
                    .filter(p -> p.getId().equals(itemDTO.getProductId()))
                    .findFirst()
                    .orElseThrow(() ->
                            new RuntimeException("Product not found for ID " + itemDTO.getProductId()));

            QuotationItem item = new QuotationItem();
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());

            // Derive price and totals
            double mrp = product.getPrice();
            double discounted = (itemDTO.getDiscountedPrice() != null)
                    ? itemDTO.getDiscountedPrice()
                    : mrp - product.getDiscountValue();

            item.setUnitPrice(discounted);
            item.setDiscountAmount(mrp - discounted);
            item.setGstRate(product.getGstRate());
            item.setTotalPrice((discounted * (1 + product.getGstRate() / 100)) * itemDTO.getQuantity());

            // back-reference
            item.setQuotation(quotation);
            return item;
        }).collect(Collectors.toList());

        quotation.setItems(items);
        return quotation;
    }
}
