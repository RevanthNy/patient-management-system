package com.wlabs.patient_service.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PatientDTO {

    @NotBlank(message = "First name is required.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    private String lastName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email should be a valid format.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^\\(?(\\d{3})\\)?[-.\\s]?(\\d{3})[-.\\s]?(\\d{4})$", message = "Invalid phone number format.")
    private String phoneNumber;

    @NotNull(message = "Height is required.")
    @Positive(message = "Height must be positive.")
    private Double heightCm;

    @NotNull(message = "Weight is required.")
    @Positive(message = "Weight must be positive.")
    private Double weightKg;

    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be in the past.")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Ethnicity is required.")
    private String ethnicity;

    @NotBlank(message = "Type of Diabetes is required.")
    private String typeOfDiabetes;

    @NotNull(message = "Date of diagnosis is required.")
    @PastOrPresent(message = "Date of diagnosis cannot be in the future.")
    private LocalDate dateOfDiagnosis;

    @NotBlank(message = "Biological sex is required.")
    private String biologicalSex;

    @NotBlank(message = "Assigned physician is required.")
    private String assignedPhysician;

    private String notes;

    @NotEmpty(message = "Medical History cannot be blank.")
    private List<String> medicalHistory;

    @NotNull(message = "Address is required.")
    @Valid
    private AddressDTO address;

    @Valid
    private List<CaregiverDTO> caregivers;
}
