package com.invenza.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuotationDTO {
    private Long id;
    private Long vendorId;
    private String vendorName;
    private List<QuotationItemDTO> items;
    private Double totalAmount;
    private LocalDateTime createdAt;


}
