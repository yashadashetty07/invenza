package com.invenza.mapper;

import com.invenza.dto.QuotationDTO;
import com.invenza.dto.QuotationItemDTO;
import com.invenza.entities.Product;
import com.invenza.entities.Quotation;
import com.invenza.entities.QuotationItem;
import com.invenza.entities.Vendor;

import java.util.List;
import java.util.stream.Collectors;

public class QuotationMapper {

    // ---------------- Entity -> DTO ----------------
    public static QuotationDTO toDTO(Quotation quotation) {
        if (quotation == null) return null;

        QuotationDTO dto = new QuotationDTO();
        dto.setId(quotation.getId());

        // vendor info
        if (quotation.getVendor() != null) {
            dto.setVendorId(quotation.getVendor().getId());
            dto.setVendorName(quotation.getVendor().getName());
        }

        // items
        if (quotation.getItems() != null) {
            dto.setItems(
                    quotation.getItems().stream()
                            .map(QuotationMapper::toItemDTO)
                            .collect(Collectors.toList())
            );
        }

        dto.setTotalAmount(quotation.getTotalAmount());
        dto.setCreatedAt(quotation.getCreatedAt());
        return dto;
    }

    private static QuotationItemDTO toItemDTO(QuotationItem item) {
        if (item == null) return null;

        QuotationItemDTO dto = new QuotationItemDTO();
        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
        }
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }

    // ---------------- DTO -> Entity ----------------
    public static Quotation toEntity(QuotationDTO dto, Vendor vendor, List<Product> products) {
        Quotation quotation = new Quotation();
        quotation.setVendor(vendor);

        List<QuotationItem> items = dto.getItems().stream().map(itemDTO -> {
            QuotationItem item = new QuotationItem();

            // Match product from DB list
            Product product = products.stream()
                    .filter(p ->((Long) p.getId()).equals(itemDTO.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Product not found for ID " + itemDTO.getProductId()));

            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice());
            item.setTotalPrice(itemDTO.getTotalPrice());
            item.setQuotation(quotation);

            return item;
        }).collect(Collectors.toList());

        quotation.setItems(items);
        return quotation;
    }

    private static QuotationItem toItemEntity(QuotationItemDTO dto, Quotation quotation, java.util.List<Product> products) {
        if (dto == null) return null;

        QuotationItem item = new QuotationItem();
        item.setQuotation(quotation);

        // find product from provided list
        Product product = products.stream()
                .filter(p -> ((Long) p.getId()).equals(dto.getProductId()))
                .findFirst()
                .orElse(null);

        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());
        item.setTotalPrice(dto.getTotalPrice());
        return item;
    }
}
