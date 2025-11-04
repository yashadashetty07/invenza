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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

        // ✅ Auto-generate bill number if missing
        if (bill.getBillNumber() == null || bill.getBillNumber().isBlank()) {
            bill.setBillNumber(generateBillNumber());
        }

        System.out.println("🚀 Creating Bill: " + bill.getBillNumber());

        double totalMRP = 0;
        double totalDiscounted = 0;
        double gstTotal = 0;
        double finalAmount = 0;

        for (BillItem item : bill.getItems()) {
            Product inputProduct = item.getProduct();
            if (inputProduct == null || inputProduct.getId() == null) {
                throw new RuntimeException("❌ Product missing or invalid");
            }

            Product managedProduct = productRepository.findById(inputProduct.getId())
                    .orElseThrow(() -> new RuntimeException("❌ Product not found with ID: " + inputProduct.getId()));

            if (managedProduct.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("❌ Insufficient Stock for " + managedProduct.getName());
            }

            // Update stock
            managedProduct.setQuantity(managedProduct.getQuantity() - item.getQuantity());
            productRepository.save(managedProduct);

            // Calculate GST, final price
            BigDecimal discountedPrice = BigDecimal.valueOf(item.getDiscountedPrice());
            BigDecimal gstRate = BigDecimal.valueOf(managedProduct.getGstRate())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal gstPerUnit = discountedPrice.multiply(gstRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal unitFinalPrice = discountedPrice.add(gstPerUnit).setScale(2, RoundingMode.HALF_UP);
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());

            item.setUnitFinalPrice(unitFinalPrice.doubleValue());
            item.setTotalFinalPrice(unitFinalPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP).doubleValue());
            item.setGstAmount(gstPerUnit.doubleValue());
            item.setBill(bill);

            totalMRP += item.getMrpPrice() * item.getQuantity();
            totalDiscounted += item.getDiscountedPrice() * item.getQuantity();
            gstTotal += gstPerUnit.multiply(quantity).doubleValue();
            finalAmount += unitFinalPrice.multiply(quantity).doubleValue();
        }

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
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Bill not found with id: " + id));
    }

    public String generateBillNumber() {
        String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
        long countToday = billRepository.count(); // total bills (you can refine by date if needed)
        String sequence = new DecimalFormat("0000").format(countToday + 1);
        return "BILL-" + datePart + "-" + sequence;
    }

    public void deleteBill(Long id) {
        Bill existing = billRepository.findBillById(id);
        billRepository.deleteById(existing.getId());
    }
    @Transactional
    public Bill updateBill(Bill bill) {
        // Reuse existing logic to recompute totals and update product quantities correctly
        double totalMRP = 0;
        double totalDiscounted = 0;
        double gstTotal = 0;
        double finalAmount = 0;

        for (BillItem item : bill.getItems()) {
            Product inputProduct = item.getProduct();
            if (inputProduct == null || inputProduct.getId() == null) {
                throw new RuntimeException("❌ Product missing or invalid");
            }

            Product managedProduct = productRepository.findById(inputProduct.getId())
                    .orElseThrow(() -> new RuntimeException("❌ Product not found with ID: " + inputProduct.getId()));

            BigDecimal discountedPrice = BigDecimal.valueOf(item.getDiscountedPrice());
            BigDecimal gstRate = BigDecimal.valueOf(managedProduct.getGstRate())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal gstPerUnit = discountedPrice.multiply(gstRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal unitFinalPrice = discountedPrice.add(gstPerUnit).setScale(2, RoundingMode.HALF_UP);
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());

            item.setUnitFinalPrice(unitFinalPrice.doubleValue());
            item.setTotalFinalPrice(unitFinalPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP).doubleValue());
            item.setGstAmount(gstPerUnit.doubleValue());
            item.setBill(bill);

            totalMRP += item.getMrpPrice() * item.getQuantity();
            totalDiscounted += item.getDiscountedPrice() * item.getQuantity();
            gstTotal += gstPerUnit.multiply(quantity).doubleValue();
            finalAmount += unitFinalPrice.multiply(quantity).doubleValue();
        }

        bill.setTotalMRP(totalMRP);
        bill.setTotalDiscounted(totalDiscounted);
        bill.setGstTotal(gstTotal);
        bill.setFinalAmount(finalAmount);

        return billRepository.save(bill);
    }

}
