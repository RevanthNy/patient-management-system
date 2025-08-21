package com.wlabs.patient_service.mapper;

import com.wlabs.patient_service.model.Patient;
import com.wlabs.patient_service.model.dto.PatientDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { CaregiverMapper.class })
public interface PatientMapper {
    Patient patientDtoToPatient(PatientDTO patientDto);

    @Mapping(target = "caregivers", ignore = true)
    void updatePatientFromDto(PatientDTO patientDto, @MappingTarget Patient patient);
}