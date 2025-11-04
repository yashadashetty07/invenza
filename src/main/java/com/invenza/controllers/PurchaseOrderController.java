package com.invenza.controllers;

import com.invenza.dto.PurchaseOrderDTO;
import com.invenza.entities.PurchaseOrderStatus;
import com.invenza.services.PurchaseOrderService;
import jakarta.validation.Valid;
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
    public ResponseEntity<PurchaseOrderDTO> createOrder(@RequestBody @Valid PurchaseOrderDTO request) throws Exception {
        return ResponseEntity.ok(purchaseOrderService.createOrder(request));
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderDTO>> getAllOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getOrderById(id));
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<PurchaseOrderDTO>> getOrdersByVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok(purchaseOrderService.getOrdersByVendor(vendorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrderDTO> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseOrderDTO orderDTO) {
        return ResponseEntity.ok(purchaseOrderService.updateOrder(id, orderDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        purchaseOrderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
