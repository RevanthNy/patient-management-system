package com.wlabs.patient_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressDTO {
    @NotBlank(message = "Mailing address is required.")
    private String mailingAddress;

    @NotBlank(message = "Zipcode is required.")
    private String zipcode;

    @NotBlank(message = "County is required.")
    private String county;

    @NotBlank(message = "State is required.")
    private String state;

    @NotBlank(message = "Country is required.")
    private String country;
}
