package com.wlabs.patient_service.model;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Entity
@Table(name = "patients")
@Data
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String phoneNumber;
    private Double heightCm;
    private Double weightKg;
    private LocalDate dateOfBirth;
    private String ethnicity;
    private String typeOfDiabetes;
    private LocalDate dateOfDiagnosis;
    private String biologicalSex;
    private String notes;

    @ElementCollection
    @CollectionTable(name = "patient_medical_history", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "condition_name")
    private List<String> medicalHistory = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "patient")
    private Address address;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "patient", orphanRemoval = true)
    private List<Caregiver> caregivers = new ArrayList<>();

    // We can have a Physician entity in a production system
    private String assignedPhysician;
}