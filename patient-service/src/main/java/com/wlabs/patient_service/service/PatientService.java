package com.wlabs.patient_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import com.wlabs.patient_service.repository.CaregiverRepository;
import com.wlabs.patient_service.repository.PatientRepository;
import com.wlabs.patient_service.mapper.CaregiverMapper;
import com.wlabs.patient_service.mapper.PatientMapper;
import com.wlabs.patient_service.model.Caregiver;
import com.wlabs.patient_service.model.Patient;
import com.wlabs.patient_service.model.dto.CaregiverDTO;
import com.wlabs.patient_service.model.dto.PatientDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;
    private final CaregiverRepository caregiverRepository;
    private final PatientMapper patientMapper;
    private final CaregiverMapper caregiverMapper;

    @Transactional
    public Patient createPatient(PatientDTO patientDto) {
        log.info("Attempting to create a new patient with email: {}", patientDto.getEmail());
        if (patientRepository.existsByFirstNameAndLastNameAndDateOfBirthAndIdNot(
                patientDto.getFirstName(), patientDto.getLastName(), patientDto.getDateOfBirth(), null)) {
            throw new IllegalArgumentException(
                    "A patient with the same first name, last name, and date of birth already exists.");
        }
        Patient patient = patientMapper.patientDtoToPatient(patientDto);

        if (patient.getAddress() != null) {
            patient.getAddress().setPatient(patient);
        }
        if (patient.getCaregivers() != null) {
            patient.getCaregivers().forEach(caregiver -> caregiver.setPatient(patient));
        }

        Patient savedPatient = patientRepository.save(patient);
        log.info("Successfully created new patient with ID: {}", savedPatient.getId());
        return savedPatient;
    }

    public Optional<Patient> getPatientById(UUID id) {
        log.info("Searching for patient with ID: {}", id);
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isPresent()) {
            log.info("Found patient with ID: {}", id);
        } else {
            log.warn("No patient found with ID: {}", id);
        }
        return patient;
    }

    @Transactional
    public Caregiver addCaregiverToPatient(UUID patientId, CaregiverDTO caregiverDto) {
        log.info("Attempting to add a new caregiver to patient ID: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.error("Failed to add caregiver. Patient not found with ID: {}", patientId);
                    return new EntityNotFoundException("Patient not found with id: " + patientId);
                });

        if (caregiverRepository.existsDuplicate(
                patientId, caregiverDto.getFirstName(), caregiverDto.getLastName(), caregiverDto.getEmail(),
                caregiverDto.getPhoneNumber(), caregiverDto.getRelationshipToPatient(), null)) {
            throw new IllegalArgumentException("A caregiver with these exact details already exists for this patient.");
        }

        Caregiver newCaregiver = caregiverMapper.caregiverDtoToCaregiver(caregiverDto);
        newCaregiver.setPatient(patient);

        Caregiver savedCaregiver = caregiverRepository.save(newCaregiver);
        log.info("Successfully added new caregiver with ID: {} to patient ID: {}", savedCaregiver.getId(), patientId);
        return savedCaregiver;
    }

    @Transactional
    public void deleteCaregiver(UUID patientId, UUID caregiverId) {
        log.info("Attempting to delete caregiver ID: {} from patient ID: {}", caregiverId, patientId);

        Caregiver caregiver = caregiverRepository.findById(caregiverId)
                .orElseThrow(() -> {
                    log.error("Failed to delete caregiver. Caregiver not found with ID: {}", caregiverId);
                    return new EntityNotFoundException("Caregiver not found with id: " + caregiverId);
                });

        if (!caregiver.getPatient().getId().equals(patientId)) {
            log.error(
                    "Security violation: Attempted to delete caregiver ID: {} which does not belong to patient ID: {}",
                    caregiverId, patientId);
            throw new SecurityException("Caregiver does not belong to the specified patient.");
        }

        caregiverRepository.deleteById(caregiverId);
        log.info("Successfully deleted caregiver ID: {} from patient ID: {}", caregiverId, patientId);
    }

    public List<Patient> searchPatients(String term) {
        log.info("Searching for patients with term: '{}'", term);
        List<Patient> patients = patientRepository.searchByTerm(term);
        log.info("Found {} patients for search term: '{}'", patients.size(), term);
        return patients;
    }

    @Transactional
    public Patient updatePatient(UUID id, PatientDTO patientDto) {
        log.info("Attempting to update patient with ID: {}", id);
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Update failed. Patient not found with id: {}", id);
                    return new EntityNotFoundException("Patient not found with id: " + id);
                });

        if (patientRepository.existsByFirstNameAndLastNameAndDateOfBirthAndIdNot(
                patientDto.getFirstName(), patientDto.getLastName(), patientDto.getDateOfBirth(), id)) {
            throw new IllegalArgumentException(
                    "An update cannot result in a duplicate patient record (same name and DOB).");
        }

        patientMapper.updatePatientFromDto(patientDto, existingPatient);

        if (patientDto.getCaregivers() != null) {
            List<Caregiver> updatedCaregivers = new ArrayList<>();
            for (CaregiverDTO dto : patientDto.getCaregivers()) {
                if (caregiverRepository.existsDuplicate(
                        id, dto.getFirstName(), dto.getLastName(), dto.getEmail(),
                        dto.getPhoneNumber(), dto.getRelationshipToPatient(), dto.getId())) {
                    throw new IllegalArgumentException(
                            "A caregiver with these exact details already exists for this patient.");
                }
                Caregiver caregiver;
                // If it's a new caregiver (no ID), check for duplicates
                if (dto.getId() == null) {
                    caregiver = caregiverMapper.caregiverDtoToCaregiver(dto);
                    caregiver.setPatient(existingPatient);
                } else {
                    // It's an existing caregiver, find and update it
                    caregiver = existingPatient.getCaregivers().stream()
                            .filter(c -> c.getId().equals(dto.getId()))
                            .findFirst()
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Caregiver with id " + dto.getId() + " not found."));

                    caregiver.setFirstName(dto.getFirstName());
                    caregiver.setLastName(dto.getLastName());
                    caregiver.setEmail(dto.getEmail());
                    caregiver.setPhoneNumber(dto.getPhoneNumber());
                    caregiver.setRelationshipToPatient(dto.getRelationshipToPatient());
                }
                updatedCaregivers.add(caregiver);
            }
            existingPatient.getCaregivers().clear();
            existingPatient.getCaregivers().addAll(updatedCaregivers);
        }

        return patientRepository.save(existingPatient);
    }

    public void deletePatient(UUID id) {
        log.info("Attempting to delete patient with ID: {}", id);
        if (!patientRepository.existsById(id)) {
            log.error("Delete failed. Patient not found with id: {}", id);
            throw new EntityNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
        log.info("Successfully deleted patient with ID: {}", id);
    }
}
