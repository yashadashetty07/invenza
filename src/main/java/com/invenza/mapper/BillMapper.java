package com.invenza.mapper;

import com.invenza.dto.BillDTO;
import com.invenza.dto.BillItemDTO;
import com.invenza.entities.Bill;
import com.invenza.entities.BillItem;
import com.invenza.entities.Product;
import com.invenza.repositories.ProductRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BillMapper {

    public static BillDTO toDTO(Bill bill) {
        System.out.println("🔁 Converting Bill entity to DTO: id=" + bill.getId() + ", billNumber=" + bill.getBillNumber());

        BillDTO dto = new BillDTO();
        dto.setId(bill.getId());
        dto.setCustomerName(bill.getCustomerName());
        dto.setBillDate(bill.getBillDate());
        dto.setTotalMRP(bill.getTotalMRP());
        dto.setTotalDiscounted(bill.getTotalDiscounted());
        dto.setGstTotal(bill.getGstTotal());
        dto.setFinalAmount(bill.getFinalAmount());
        dto.setBillNumber(bill.getBillNumber());
        dto.setCustomerAddress(bill.getCustomerAddress());
        dto.setCustomerGSTIN(bill.getCustomerGSTIN());

        List<BillItemDTO> itemDTOs = bill.getItems().stream()
                .filter(Objects::nonNull)
                .map(item -> {
                    BillItemDTO itemDTO = new BillItemDTO();
                    itemDTO.setProductId(item.getProduct().getId()); // ✅ Corrected: use Product ID
                    itemDTO.setProductName(item.getProductName());
                    itemDTO.setHsnCode(item.getHsnCode());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setMrpPrice(item.getMrpPrice());
                    itemDTO.setDiscountedPrice(item.getDiscountedPrice());
                    itemDTO.setGstAmount(item.getGstAmount());
                    itemDTO.setUnitFinalPrice(item.getUnitFinalPrice());
                    itemDTO.setTotalFinalPrice(item.getTotalFinalPrice());
                    return itemDTO;
                }).collect(Collectors.toList());

        dto.setItems(itemDTOs);
        System.out.println("✅ DTO conversion complete: id=" + dto.getId() + ", itemsCount=" + (dto.getItems()==null?0:dto.getItems().size()));
        return dto;
    }

    public static Bill fromDTO(BillDTO dto, ProductRepository productRepository) {
        System.out.println("🔍 Mapping BillDTO to Bill: " + dto);

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("❌ Bill must contain at least one item");
        }

        Bill bill = new Bill(); // ✅ FIXED: initialize the Bill object

        bill.setCustomerName(dto.getCustomerName());
        bill.setBillDate(dto.getBillDate());
        bill.setCustomerAddress(dto.getCustomerAddress());
        bill.setCustomerGSTIN(dto.getCustomerGSTIN());
        bill.setBillNumber(dto.getBillNumber());

        List<BillItem> items = dto.getItems().stream()
                .filter(Objects::nonNull)
                .map(itemDTO -> {
                    System.out.println("➡️ Mapping BillItemDTO: " + itemDTO);

                    if (itemDTO.getProductId() == null) {
                        throw new IllegalArgumentException("❌ Product ID is missing in BillItemDTO");
                    }

                    Product product = productRepository.findById(itemDTO.getProductId())
                            .orElseThrow(() -> new RuntimeException("❌ Product not found with ID: " + itemDTO.getProductId()));

                    System.out.println("✅ Found Product: " + product);

                    BillItem item = new BillItem();
                    item.setProduct(product);
                    item.setProductName(product.getName());
                    item.setHsnCode(product.getHsnCode());
                    item.setMrpPrice(product.getPrice());
                    item.setQuantity(itemDTO.getQuantity());
                    item.setDiscountedPrice(itemDTO.getDiscountedPrice() > 0 ? itemDTO.getDiscountedPrice() : product.getPrice());

                    double gstPerUnit = product.getGstRate() / 100 * item.getDiscountedPrice();
                    double unitFinalPrice = item.getDiscountedPrice() + gstPerUnit;

                    item.setGstAmount(gstPerUnit);
                    item.setUnitFinalPrice(unitFinalPrice);
                    item.setTotalFinalPrice(unitFinalPrice * item.getQuantity());

                    System.out.println("🧮 Calculated BillItem: " + item);

                    item.setBill(bill); // ✅ Link to parent
                    return item;
                }).collect(Collectors.toList());

        bill.setItems(items);

        double totalMRP = items.stream().mapToDouble(i -> i.getMrpPrice() * i.getQuantity()).sum();
        double totalDiscounted = items.stream().mapToDouble(i -> i.getDiscountedPrice() * i.getQuantity()).sum();
        double gstTotal = items.stream().mapToDouble(i -> i.getGstAmount() * i.getQuantity()).sum();
        double finalAmount = items.stream().mapToDouble(BillItem::getTotalFinalPrice).sum();

        bill.setTotalMRP(totalMRP);
        bill.setTotalDiscounted(totalDiscounted);
        bill.setGstTotal(gstTotal);
        bill.setFinalAmount(finalAmount);

        System.out.println("📊 Final Bill Totals — MRP: " + totalMRP + ", Discounted: " + totalDiscounted + ", GST: " + gstTotal + ", Final: " + finalAmount);

        return bill;
    }
}
