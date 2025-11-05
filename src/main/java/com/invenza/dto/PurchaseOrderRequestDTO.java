package com.invenza.dto;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseOrderRequestDTO {

    private Long vendorId;
    private List<PurchaseOrderItemRequestDTO> items;
}
