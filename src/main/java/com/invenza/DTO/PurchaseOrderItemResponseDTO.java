package com.invenza.DTO;

import com.invenza.entities.PurchaseOrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderItemResponseDTO {
    private Long productId;
    private Double quantity;
    private Double price;
    private Double subtotal;

    public static PurchaseOrderItemResponseDTO fromEntity(PurchaseOrderItem item) {
        return new PurchaseOrderItemResponseDTO(
                item.getProduct().getId(),
                item.getQuantity(),
                item.getPrice(),
                item.getSubtotal()
        );
    }
}
