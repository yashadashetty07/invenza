package com.invenza.services;

import com.invenza.entities.Bill;
import com.invenza.entities.BillItem;
import com.invenza.entities.Product;
import com.invenza.repositories.BillItemsRepository;
import com.invenza.repositories.BillRepository;
import com.invenza.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
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
            Product product = productRepository.findById(item.getProduct().getId()).orElseThrow(() -> new RuntimeException("Product not found"));
            item.setHsnCode(product.getHsnCode());
            item.setProductName(product.getName());
            item.setMrpPrice(product.getPrice());

            //discount calculation
            if (item.getDiscountedPrice() <= 0) {
                item.setDiscountedPrice(product.getPrice());
            }

            //stock validation
            if (product.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient Stock of " + product.getName());
            }

            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            double gstAmount = product.getGstPercentage() / 100 * item.getDiscountedPrice();

            double itemFinalPrice = item.getDiscountedPrice() + gstAmount;

            totalMRP += item.getMrpPrice() * item.getQuantity();
            totalDiscounted += item.getDiscountedPrice() * item.getQuantity();
            gstTotal += gstAmount * item.getQuantity();
            finalAmount += itemFinalPrice * item.getQuantity();
            item.setFinalPrice(itemFinalPrice);
            item.setGstAmount(gstAmount);
            item.setBill(bill);

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
        return billRepository.findBillById(id);
    }
}
