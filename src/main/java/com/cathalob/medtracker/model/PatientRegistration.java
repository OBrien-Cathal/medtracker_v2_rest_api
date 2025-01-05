package com.cathalob.medtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity(name = "PATIENT_REGISTRATION")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PatientRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USERMODEL_ID", nullable = false)
    private UserModel userModel;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRACTITIONER_ID", nullable = false)
    private UserModel practitionerUserModel;

    @NotNull
    private boolean registered = false;
}
