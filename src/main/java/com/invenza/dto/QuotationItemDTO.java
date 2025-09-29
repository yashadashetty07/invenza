package com.invenza.dto;

import lombok.Data;

@Data
public class QuotationItemDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;

    // Getters and Setters
}
