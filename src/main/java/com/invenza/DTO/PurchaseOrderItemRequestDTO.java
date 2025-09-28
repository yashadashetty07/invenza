package com.invenza.DTO;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class PurchaseOrderItemRequestDTO {

    private Long productId;
        private Double quantity;
        private Double unitPrice;
    }

