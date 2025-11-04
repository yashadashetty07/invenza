package com.invenza.dto;

import com.invenza.entities.Vendor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String gstNumber;

    public static VendorDTO fromEntity(Vendor vendor) {
        return new VendorDTO(
                vendor.getId(),
                vendor.getName(),
                vendor.getEmail(),
                vendor.getPhone(),
                vendor.getAddress(),
                vendor.getGstNumber()
        );
    }
}
