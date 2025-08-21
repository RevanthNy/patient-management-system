package com.wlabs.patient_service.service;

import com.wlabs.patient_service.mapper.PatientMapper;
import com.wlabs.patient_service.model.Patient;
import com.wlabs.patient_service.model.dto.PatientDTO;
import com.wlabs.patient_service.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private PatientDTO patientDto;
    private UUID patientId;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        patient = new Patient();
        patient.setId(patientId);
        patientDto = new PatientDTO();
    }

    @Test
    void whenCreatePatient_thenSaveAndReturnPatient() {

        when(patientMapper.patientDtoToPatient(any(PatientDTO.class))).thenReturn(patient);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient savedPatient = patientService.createPatient(patientDto);

        assertThat(savedPatient).isNotNull();
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    void whenGetPatientById_thenReturnPatient() {

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        Optional<Patient> foundPatient = patientService.getPatientById(patientId);

        assertThat(foundPatient).isPresent();
        assertThat(foundPatient.get().getId()).isEqualTo(patientId);
    }

    @Test
    void whenDeletePatient_thenRepositoryDeleteIsCalled() {

        when(patientRepository.existsById(patientId)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(patientId);

        patientService.deletePatient(patientId);

        verify(patientRepository, times(1)).deleteById(patientId);
    }
}