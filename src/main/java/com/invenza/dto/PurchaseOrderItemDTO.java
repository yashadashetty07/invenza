package com.invenza.dto;

import com.invenza.entities.PurchaseOrderItem;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderItemDTO {

    // ✅ Request + Response field
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Double quantity;

    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be greater than zero")
    private Double price;

    // ✅ Response-only field
    private Double subtotal;

    // ✅ Static mapper for response
    public static PurchaseOrderItemDTO fromEntity(PurchaseOrderItem item) {
        return new PurchaseOrderItemDTO(
                item.getProduct().getId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}
