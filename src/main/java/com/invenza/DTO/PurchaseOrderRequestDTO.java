package com.invenza.DTO;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Data
public class PurchaseOrderRequestDTO {

    private Long vendorId;
    private List<PurchaseOrderItemRequestDTO> items;
}
