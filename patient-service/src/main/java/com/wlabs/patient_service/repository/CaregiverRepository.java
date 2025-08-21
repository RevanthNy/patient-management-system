package com.wlabs.patient_service.repository;

import com.wlabs.patient_service.model.Caregiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CaregiverRepository extends JpaRepository<Caregiver, UUID> {

    // Check if a caregiver with the same details exists for a specific patient
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM Caregiver c WHERE " +
            "c.patient.id = :patientId AND " +
            "c.firstName = :firstName AND " +
            "c.lastName = :lastName AND " +
            "c.email = :email AND " +
            "c.phoneNumber = :phoneNumber AND " +
            "c.relationshipToPatient = :relationshipToPatient AND " +
            "(:caregiverId IS NULL OR c.id != :caregiverId)")
    boolean existsDuplicate(
            @Param("patientId") UUID patientId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("relationshipToPatient") String relationshipToPatient,
            @Param("caregiverId") UUID caregiverId);
}
