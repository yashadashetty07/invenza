package com.invenza.services;

import com.invenza.DTO.PurchaseOrderItemRequestDTO;
import com.invenza.DTO.PurchaseOrderRequestDTO;
import com.invenza.DTO.PurchaseOrderResponseDTO;
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
    public PurchaseOrderResponseDTO createOrder(PurchaseOrderRequestDTO request) throws Exception {
        Vendor vendor = vendorRepository.findById(request.getVendorId()).orElseThrow(() -> new Exception("Vendor not found with id: " + request.getVendorId()));

        PurchaseOrder order = new PurchaseOrder();
        order.setVendor(vendor);
        order.setStatus(PurchaseOrderStatus.PENDING);

        List<PurchaseOrderItem> items = new ArrayList<>();
        double total = 0.0;

        for (PurchaseOrderItemRequestDTO itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId()).orElseThrow(() -> new Exception("Product not found with id: " + itemReq.getProductId()));

            double qty = itemReq.getQuantity() != null ? itemReq.getQuantity() : 1;
            double price = itemReq.getUnitPrice() > 0 ? itemReq.getUnitPrice() : Math.max(product.getPrice(), 0.0);

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setProduct(product);
            item.setQuantity(qty);
            item.setPrice(price);
            item.setSubtotal(price * qty);
            item.setPurchaseOrder(order);
            items.add(item);

            total += item.getSubtotal();
        }

        order.setItems(items);
        order.setTotalAmount(total);

        PurchaseOrder saved = purchaseOrderRepository.save(order);

        return PurchaseOrderResponseDTO.fromEntity(saved);
    }

    public List<PurchaseOrderResponseDTO> getAllOrders() {
        return purchaseOrderRepository.findAll().stream().map(PurchaseOrderResponseDTO::fromEntity).toList();
    }

    public PurchaseOrderResponseDTO getOrderById(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        return PurchaseOrderResponseDTO.fromEntity(order);
    }

    public List<PurchaseOrderResponseDTO> getOrdersByVendor(Long id) {
        return purchaseOrderRepository.findByVendor_Id(id).stream().map(PurchaseOrderResponseDTO::fromEntity).toList();
    }

    public PurchaseOrderResponseDTO updateOrderStatus(Long orderId, PurchaseOrderStatus status) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        PurchaseOrder updated = purchaseOrderRepository.save(order);
        return PurchaseOrderResponseDTO.fromEntity(updated);
    }

    public void deleteOrder(Long id) {
        purchaseOrderRepository.deleteById(id);
    }
}
