package com.invenza.dto;

import lombok.Data;

@Data
public class PurchaseOrderItemRequestDTO {

    private Long productId;
        private Double quantity;
        private Double unitPrice;
    }

