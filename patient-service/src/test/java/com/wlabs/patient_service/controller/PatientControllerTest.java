package com.wlabs.patient_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wlabs.patient_service.model.Patient;
import com.wlabs.patient_service.model.dto.PatientDTO;
import com.wlabs.patient_service.model.dto.AddressDTO;
import com.wlabs.patient_service.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    private Patient patient;
    private PatientDTO patientDto;
    private UUID patientId;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();

        patient = new Patient();
        patient.setId(patientId);
        patient.setFirstName("John");
        patient.setLastName("Doe");

        // Initialize patientDto with valid data
        patientDto = new PatientDTO();
        patientDto.setFirstName("John");
        patientDto.setLastName("Doe");
        patientDto.setEmail("john.doe@example.com");
        patientDto.setPhoneNumber("555-123-4567");
        patientDto.setHeightCm(180.0);
        patientDto.setWeightKg(80.0);
        patientDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patientDto.setEthnicity("White");
        patientDto.setTypeOfDiabetes("Type 1");
        patientDto.setDateOfDiagnosis(LocalDate.of(2010, 5, 15));
        patientDto.setBiologicalSex("Male");
        patientDto.setAssignedPhysician("Dr. Jane Doe");

        // Initialize address with valid data
        AddressDTO addressDto = new AddressDTO();
        addressDto.setMailingAddress("123 Main St");
        addressDto.setZipcode("90210");
        addressDto.setState("CA");
        addressDto.setCountry("USA");
        patientDto.setAddress(addressDto);

        // Initialize caregivers and medical history
        patientDto.setCaregivers(Collections.emptyList());
        patientDto.setMedicalHistory(Collections.singletonList("None")); // Must be @NotEmpty
    }

    @Test
    void whenGetPatientById_thenReturnPatient() throws Exception {
        given(patientService.getPatientById(patientId)).willReturn(Optional.of(patient));

        mockMvc.perform(get("/api/patients/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patientId.toString()));
    }

    @Test
    void whenSearchPatients_thenReturnPatientList() throws Exception {
        given(patientService.searchPatients("John")).willReturn(Collections.singletonList(patient));

        mockMvc.perform(get("/api/patients/search").param("term", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void whenDeletePatient_thenReturnNoContent() throws Exception {
        doNothing().when(patientService).deletePatient(patientId);

        mockMvc.perform(delete("/api/patients/{id}", patientId))
                .andExpect(status().isNoContent());
    }
}