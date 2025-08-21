package com.wlabs.patient_service.mapper;

import com.wlabs.patient_service.model.Caregiver;
import com.wlabs.patient_service.model.dto.CaregiverDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CaregiverMapper {
    Caregiver caregiverDtoToCaregiver(CaregiverDTO caregiverDto);
}
