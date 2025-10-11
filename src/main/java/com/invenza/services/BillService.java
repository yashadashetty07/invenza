package com.invenza.services;

import com.invenza.dto.BillItemDTO;
import com.invenza.entities.Bill;
import com.invenza.entities.BillItem;
import com.invenza.entities.Product;
import com.invenza.repositories.BillItemsRepository;
import com.invenza.repositories.BillRepository;
import com.invenza.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillItemsRepository billItemsRepository;

    @Autowired
    private ProductRepository productRepository;

    private BillItemDTO billItemDTO;

    @Transactional
    public Bill createBill(Bill bill) {
        System.out.println("🚀 Starting Bill creation: billNumber=" + bill.getBillNumber()
                + ", customer=" + bill.getCustomerName() + ", itemsCount=" + (bill.getItems() == null ? 0 : bill.getItems().size()));

        double totalMRP = 0;
        double totalDiscounted = 0;
        double gstTotal = 0;
        double finalAmount = 0;

        for (BillItem item : bill.getItems()) {
            System.out.println("🔄 Processing BillItem: productId=" + (item.getProduct() != null ? item.getProduct().getId() : "null")
                    + ", qty=" + item.getQuantity() + ", discountedPrice=" + item.getDiscountedPrice());

            Product inputProduct = item.getProduct();
            if (inputProduct == null || inputProduct.getId() == null) {
                throw new RuntimeException("❌ Product is missing or has no ID in BillItem");
            }

            Product managedProduct = productRepository.findById(inputProduct.getId())
                    .orElseThrow(() -> new RuntimeException("❌ Product not found with ID: " + inputProduct.getId()));

            System.out.println("✅ Loaded Product from DB: " + managedProduct);

            if (managedProduct.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("❌ Insufficient Stock of " + managedProduct.getName());
            }

            managedProduct.setQuantity(managedProduct.getQuantity() - item.getQuantity());
            productRepository.save(managedProduct);
            System.out.println("📦 Updated Product stock: " + managedProduct.getQuantity());

            // Repaired calculation using BigDecimal
            BigDecimal discountedPrice = BigDecimal.valueOf(item.getDiscountedPrice());
            BigDecimal gstRate = BigDecimal.valueOf(managedProduct.getGstRate()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal gstPerUnit = discountedPrice.multiply(gstRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal unitFinalPrice = discountedPrice.add(gstPerUnit).setScale(2, RoundingMode.HALF_UP);
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());

            item.setUnitFinalPrice(unitFinalPrice.doubleValue());
            item.setTotalFinalPrice(unitFinalPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP).doubleValue());
            item.setGstAmount(gstPerUnit.doubleValue());
            item.setBill(bill);

            // Optional: CGST/SGST split (if needed in DTO/entity)
            // double cgst = gstPerUnit.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP).doubleValue();
            // double sgst = cgst;

            totalMRP += item.getMrpPrice() * item.getQuantity();
            totalDiscounted += item.getDiscountedPrice() * item.getQuantity();
            gstTotal += gstPerUnit.multiply(quantity).setScale(2, RoundingMode.HALF_UP).doubleValue();
            finalAmount += unitFinalPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP).doubleValue();

            System.out.println("🧾 Finalized BillItem: productId=" + item.getProduct().getId()
                    + ", totalFinalPrice=" + item.getTotalFinalPrice());
        }

        bill.setTotalMRP(totalMRP);
        bill.setTotalDiscounted(totalDiscounted);
        bill.setGstTotal(gstTotal);
        bill.setFinalAmount(finalAmount);

        System.out.println("📥 Saving Bill: billNumber=" + bill.getBillNumber() + ", finalAmount=" + bill.getFinalAmount());
        return billRepository.save(bill);
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Bill getBillById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Bill not found with id: " + id));
    }
}
