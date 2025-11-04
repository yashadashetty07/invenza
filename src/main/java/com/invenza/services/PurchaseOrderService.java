package com.invenza.services;

import com.invenza.dto.PurchaseOrderDTO;
import com.invenza.dto.PurchaseOrderItemDTO;
import com.invenza.entities.*;
import com.invenza.repositories.ProductRepository;
import com.invenza.repositories.PurchaseOrderRepository;
import com.invenza.repositories.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public PurchaseOrderDTO createOrder(PurchaseOrderDTO request) throws Exception {
        Vendor vendor = vendorRepository.findById(request.getVendorId()).orElseThrow(() -> new Exception("Vendor not found with id: " + request.getVendorId()));

        PurchaseOrder order = new PurchaseOrder();
        order.setVendor(vendor);
        order.setStatus(request.getStatus() != null ? request.getStatus() : PurchaseOrderStatus.PENDING);

        List<PurchaseOrderItem> items = new ArrayList<>();
        double total = 0.0;

        for (PurchaseOrderItemDTO itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId()).orElseThrow(() -> new Exception("Product not found with id: " + itemReq.getProductId()));

            double qty = itemReq.getQuantity() != null ? itemReq.getQuantity() : 1;
            double price = itemReq.getPrice() > 0 ? itemReq.getPrice() : Math.max(product.getPrice(), 0.0);

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setProduct(product);
            item.setQuantity(qty);
            item.setUnitPrice(price);
            item.setSubtotal(price * qty);
            item.setPurchaseOrder(order);
            items.add(item);

            total += item.getSubtotal();
        }

        order.setItems(items);
        order.setTotalAmount(total);

        PurchaseOrder saved = purchaseOrderRepository.save(order);

        return PurchaseOrderDTO.fromEntity(saved);
    }

    public List<PurchaseOrderDTO> getAllOrders() {
        return purchaseOrderRepository.findAll().stream().map(PurchaseOrderDTO::fromEntity).toList();
    }

    public PurchaseOrderDTO getOrderById(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        return PurchaseOrderDTO.fromEntity(order);
    }

    public List<PurchaseOrderDTO> getOrdersByVendor(Long id) {
        return purchaseOrderRepository.findByVendor_Id(id).stream().map(PurchaseOrderDTO::fromEntity).toList();
    }

    @Transactional
    public PurchaseOrderDTO updateOrder(Long orderId, PurchaseOrderDTO orderDTO) {
        PurchaseOrder existingOrder = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Purchase order not found"));

        // ✅ Update Vendor
        if (orderDTO.getVendorId() != null) {
            Vendor vendor = vendorRepository.findById(orderDTO.getVendorId())
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));
            existingOrder.setVendor(vendor);
        }

        // ✅ Update Status
        if (orderDTO.getStatus() != null) {
            existingOrder.setStatus(orderDTO.getStatus());
        }

        List<PurchaseOrderItem> updatedItems = existingOrder.getItems();
        updatedItems.clear(); // Hibernate tracks this as orphan removal

        for (PurchaseOrderItemDTO itemDTO : orderDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + itemDTO.getProductId()));
            product.setQuantity(product.getQuantity() + itemDTO.getQuantity());
            productRepository.save(product);

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getPrice());
            item.setSubtotal(itemDTO.getQuantity() * itemDTO.getPrice());
            item.setPurchaseOrder(existingOrder); // 🔥 Critical for bidirectional link

            updatedItems.add(item); // ✅ Modify in-place
        }

        // ✅ Recalculate total amount
        double total = updatedItems.stream()
                .mapToDouble(PurchaseOrderItem::getSubtotal)
                .sum();
        existingOrder.setTotalAmount(total);

        // ✅ Update timestamps automatically via @PreUpdate (if present)
        PurchaseOrder updated = purchaseOrderRepository.save(existingOrder);
        return PurchaseOrderDTO.fromEntity(updated);
    }


    public void deleteOrder(Long id) {
        purchaseOrderRepository.deleteById(id);
    }
}
