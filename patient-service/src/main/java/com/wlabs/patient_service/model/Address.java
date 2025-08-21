package com.wlabs.patient_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "addresses")
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private String mailingAddress;
    private String zipcode;
    private String county;
    private String state;
    private String country;

    @OneToOne
    @JoinColumn(name = "patient_id")
    @JsonIgnore // Prevents infinite recursion in JSON serialization
    private Patient patient;
}
