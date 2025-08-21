package com.wlabs.patient_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.wlabs.patient_service.model.dto.CaregiverDTO;
import com.wlabs.patient_service.model.Caregiver;

import static org.assertj.core.api.Assertions.assertThat;

class CaregiverMapperTest {

    private final CaregiverMapper caregiverMapper = Mappers.getMapper(CaregiverMapper.class);

    @Test
    void whenMapCaregiverDtoToCaregiver_thenCorrectlyMapped() {
        CaregiverDTO dto = new CaregiverDTO();
        dto.setFirstName("Sarah");
        dto.setLastName("Connor");
        dto.setEmail("sarah.connor@example.com");
        dto.setPhoneNumber("123-555-1984");
        dto.setRelationshipToPatient("Mother");

        Caregiver caregiver = caregiverMapper.caregiverDtoToCaregiver(dto);

        assertThat(caregiver).isNotNull();
        assertThat(caregiver.getFirstName()).isEqualTo("Sarah");
        assertThat(caregiver.getLastName()).isEqualTo("Connor");
        assertThat(caregiver.getEmail()).isEqualTo("sarah.connor@example.com");
        assertThat(caregiver.getPhoneNumber()).isEqualTo("123-555-1984");
        assertThat(caregiver.getRelationshipToPatient()).isEqualTo("Mother");
    }
}
