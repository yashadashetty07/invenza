package com.invenza.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuotationDTO {
    private Long id;
    private String customerName;
    private String customerAddress;
    private String customerGSTIN;
    private List<QuotationItemDTO> items;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private String quotationNumber;
}
