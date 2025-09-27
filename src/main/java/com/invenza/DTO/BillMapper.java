package com.invenza.DTO;

import com.invenza.entities.Bill;
import java.util.List;
import java.util.stream.Collectors;

public class BillMapper {

    public static BillDTO toDTO(Bill bill) {
        BillDTO dto = new BillDTO();
        dto.setId(bill.getId());
        dto.setCustomerName(bill.getCustomerName());
        dto.setBillDate(bill.getBillDate());
        dto.setTotalMRP(bill.getTotalMRP());
        dto.setTotalDiscounted(bill.getTotalDiscounted());
        dto.setGstTotal(bill.getGstTotal());
        dto.setFinalAmount(bill.getFinalAmount());

        List<BillItemDTO> itemDTOs = bill.getItems().stream().map(item -> {
            BillItemDTO itemDTO = new BillItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setProductName(item.getProductName());
            itemDTO.setHsnCode(item.getHsnCode());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setMrpPrice(item.getMrpPrice());
            itemDTO.setDiscountedPrice(item.getDiscountedPrice());
            itemDTO.setGstAmount(item.getGstAmount());
            itemDTO.setUnitFinalPrice(item.getUnitFinalPrice()); // unit price including GST
            itemDTO.setTotalFinalPrice(item.getTotalFinalPrice());     // total price for this item = unitFinalPrice * quantity
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }
}
