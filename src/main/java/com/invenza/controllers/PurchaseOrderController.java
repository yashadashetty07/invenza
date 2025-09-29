package com.invenza.controllers;

import com.invenza.dto.PurchaseOrderRequestDTO;
import com.invenza.dto.PurchaseOrderResponseDTO;
import com.invenza.entities.PurchaseOrderStatus;
import com.invenza.services.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrderResponseDTO> createOrder(@RequestBody PurchaseOrderRequestDTO request) throws Exception {
        return ResponseEntity.ok(purchaseOrderService.createOrder(request));
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponseDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getOrderById(id));
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getOrdersByVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok(purchaseOrderService.getOrdersByVendor(vendorId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PurchaseOrderResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam PurchaseOrderStatus status) {
        return ResponseEntity.ok(purchaseOrderService.updateOrderStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        purchaseOrderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
