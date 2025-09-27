package com.invenza.services;

import com.invenza.entities.Bill;
import com.invenza.entities.BillItem;
import com.invenza.entities.Product;
import com.invenza.repositories.BillItemsRepository;
import com.invenza.repositories.BillRepository;
import com.invenza.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillItemsRepository billItemsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Bill createBill(Bill bill) {
        double totalMRP = 0;
        double totalDiscounted = 0;
        double gstTotal = 0;
        double finalAmount = 0;

        for (BillItem item : bill.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Set product details
            item.setHsnCode(product.getHsnCode());
            item.setProductName(product.getName());
            item.setMrpPrice(product.getPrice());

            // Skip discount → take same as MRP
            if (item.getDiscountedPrice() <= 0) {
                item.setDiscountedPrice(product.getPrice());
            }

            // Stock validation
            if (product.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient Stock of " + product.getName());
            }

            // Deduct stock
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            // GST and final price (per unit)
            double gstPerUnit = product.getGstPercentage() / 100 * item.getDiscountedPrice();
            double unitFinalPrice = item.getDiscountedPrice() + gstPerUnit;

            // Totals
            totalMRP += item.getMrpPrice() * item.getQuantity();
            totalDiscounted += item.getDiscountedPrice() * item.getQuantity();
            gstTotal += gstPerUnit * item.getQuantity();
            finalAmount += unitFinalPrice * item.getQuantity();

            // Set item details
            item.setUnitFinalPrice(unitFinalPrice);
            item.setTotalFinalPrice(unitFinalPrice * item.getQuantity());
            item.setGstAmount(gstPerUnit);
            item.setBill(bill);
            item.setUnitFinalPrice(unitFinalPrice);
        }

        // Set bill totals
        bill.setTotalMRP(totalMRP);
        bill.setTotalDiscounted(totalDiscounted);
        bill.setGstTotal(gstTotal);
        bill.setFinalAmount(finalAmount);

        return billRepository.save(bill);
    }


    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Bill getBillById(Long id) {
        return billRepository.findBillById(id);
    }
}
