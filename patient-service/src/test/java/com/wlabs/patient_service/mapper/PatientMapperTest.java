package com.wlabs.patient_service.mapper;

import com.wlabs.patient_service.model.Patient;
import com.wlabs.patient_service.model.dto.PatientDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class PatientMapperTest {

    private final PatientMapper patientMapper = Mappers.getMapper(PatientMapper.class);

    @Test
    void whenMapPatientDtoToPatient_thenCorrectlyMapped() {
        PatientDTO dto = new PatientDTO();
        dto.setFirstName("Jane");
        dto.setLastName("Doe");
        dto.setEmail("jane.doe@example.com");

        Patient patient = patientMapper.patientDtoToPatient(dto);

        assertThat(patient).isNotNull();
        assertThat(patient.getFirstName()).isEqualTo("Jane");
        assertThat(patient.getLastName()).isEqualTo("Doe");
        assertThat(patient.getEmail()).isEqualTo("jane.doe@example.com");
    }
}