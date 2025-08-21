package com.wlabs.patient_service.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.util.UUID;

@Data
public class CaregiverDTO {

    private UUID id;

    @NotBlank(message = "First name is required.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    private String lastName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email should be valid.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^\\(?(\\d{3})\\)?[-.\\s]?(\\d{3})[-.\\s]?(\\d{4})$", message = "Invalid phone number format.")
    private String phoneNumber;

    @NotBlank(message = "Relationship to patient is required.")
    private String relationshipToPatient;
}
