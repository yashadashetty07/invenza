package com.invenza.dto;

import com.invenza.entities.PurchaseOrder;
import com.invenza.entities.PurchaseOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderResponseDTO {

    private Long id;
    private Long vendorId;
    private PurchaseOrderStatus status;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PurchaseOrderItemResponseDTO> items;

    public static PurchaseOrderResponseDTO fromEntity(PurchaseOrder order) {
        PurchaseOrderResponseDTO dto = new PurchaseOrderResponseDTO();
        dto.setId(order.getId());
        dto.setVendorId(order.getVendor().getId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        dto.setItems(
                order.getItems().stream()
                        .map(PurchaseOrderItemResponseDTO::fromEntity)
                        .collect(Collectors.toList())
        );
        return dto;
    }
}
