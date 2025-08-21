package com.wlabs.patient_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import com.wlabs.patient_service.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    @Query("SELECT p FROM Patient p WHERE lower(p.firstName) LIKE lower(concat('%', :term, '%')) OR " +
            "lower(p.lastName) LIKE lower(concat('%', :term, '%')) OR " +
            "lower(p.email) LIKE lower(concat('%', :term, '%')) OR " +
            "p.phoneNumber LIKE concat('%', :term, '%')")
    List<Patient> searchByTerm(@Param("term") String term);

    // Check if a patient with the same first name, last name, and date of birth exists, excluding the patient with the given ID
    boolean existsByFirstNameAndLastNameAndDateOfBirthAndIdNot(String firstName, String lastName, LocalDate dateOfBirth, UUID id);
}