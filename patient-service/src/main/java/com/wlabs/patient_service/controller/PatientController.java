package com.wlabs.patient_service.controller;

import com.wlabs.patient_service.service.PatientService;

import jakarta.validation.Valid;

import com.wlabs.patient_service.model.Caregiver;
import com.wlabs.patient_service.model.Patient;
import com.wlabs.patient_service.model.dto.CaregiverDTO;
import com.wlabs.patient_service.model.dto.PatientDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody PatientDTO patientDto) {
        Patient createdPatient = patientService.createPatient(patientDto);
        log.debug("Created patient with ID: " + createdPatient.getId());
        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable UUID id) {
        log.debug("Fetching patient with ID: " + id);
        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{patientId}/caregivers")
    public ResponseEntity<Caregiver> addCaregiver(
            @PathVariable UUID patientId,
            @Valid @RequestBody CaregiverDTO caregiverDto) {
        log.debug("Adding caregiver for patient with ID: " + patientId);
        Caregiver savedCaregiver = patientService.addCaregiverToPatient(patientId, caregiverDto);
        return new ResponseEntity<>(savedCaregiver, HttpStatus.CREATED);
    }

    @DeleteMapping("/{patientId}/caregivers/{caregiverId}")
    public ResponseEntity<Void> deleteCaregiver(
            @PathVariable UUID patientId,
            @PathVariable UUID caregiverId) {
        log.debug("Deleting caregiver with ID: " + caregiverId + " for patient with ID: " + patientId);
        patientService.deleteCaregiver(patientId, caregiverId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam String term) {
        log.info("searching patients with term: " + term);
        return ResponseEntity.ok(patientService.searchPatients(term));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable UUID id, @Valid @RequestBody PatientDTO patientDto) {
        log.debug("updating patient with ID: " + id);
        return ResponseEntity.ok(patientService.updatePatient(id, patientDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        log.debug("deleting patient with ID: " + id);
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
