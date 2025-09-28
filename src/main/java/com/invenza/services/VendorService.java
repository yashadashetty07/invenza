package com.invenza.services;

import com.invenza.entities.PurchaseOrder;
import com.invenza.entities.Vendor;
import com.invenza.repositories.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    public Vendor getVendorById(Long id) {
        return vendorRepository.findByid(id);
    }

    public Vendor createVendor(Vendor vendor) {
        if (vendorRepository.existsByEmail(vendor.getEmail())) {
            throw new IllegalArgumentException("Vendor with this email already exists");
        }
        if (vendorRepository.existsByPhone(vendor.getPhone())) {
            throw new IllegalArgumentException("Vendor with this phone number already exists");
        }
        if (vendorRepository.existsByGstNumber(vendor.getGstNumber())) {
            throw new IllegalArgumentException("Vendor with this GST number already exists");
        }
        return vendorRepository.save(vendor);
    }

    public Vendor updateVendor(Long id, Vendor updatedVendor) {
        Vendor existingVendor = getVendorById(id);

        existingVendor.setName(updatedVendor.getName());
        existingVendor.setAddress(updatedVendor.getAddress());
        existingVendor.setEmail(updatedVendor.getEmail());
        existingVendor.setPhone(updatedVendor.getPhone());
        existingVendor.setGstNumber(updatedVendor.getGstNumber());

        // ✅ Safely update purchaseOrders
        existingVendor.getPurchaseOrders().clear();
        existingVendor.getPurchaseOrders().addAll(updatedVendor.getPurchaseOrders());

        for (PurchaseOrder order : existingVendor.getPurchaseOrders()) {
            order.setVendor(existingVendor); // ✅ Maintain bidirectional link
        }

        return vendorRepository.save(existingVendor);
    }


    public void deleteVendor(Long id) {
        Vendor savedVendor = vendorRepository.findByid(id);
        vendorRepository.delete(savedVendor);
    }
}