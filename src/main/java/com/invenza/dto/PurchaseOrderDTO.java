package com.invenza.dto;

import com.invenza.entities.PurchaseOrder;
import com.invenza.entities.PurchaseOrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PurchaseOrderDTO {

    // ✅ Response-only fields
    private Long id;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
                                                       
    // ✅ Shared fields
    @NotNull(message = "Vendor ID is required")
    private Long vendorId;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<PurchaseOrderItemDTO> items;

    @NotNull(message = "Status is required")
    private PurchaseOrderStatus status;

    // ✅ Static mapper for response
    public static PurchaseOrderDTO fromEntity(PurchaseOrder order) {
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setId(order.getId());
        dto.setVendorId(order.getVendor().getId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        dto.setItems(
                order.getItems().stream()
                        .map(PurchaseOrderItemDTO::fromEntity)
                        .collect(Collectors.toList())
        );
        return dto;
    }
}
