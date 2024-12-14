package com.cathalob.medtracker.model.prescription;

import com.cathalob.medtracker.model.UserModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity(name = "PRESCRIPTION")
@AllArgsConstructor
@NoArgsConstructor
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int doseMg;

    @OneToOne
    @JoinColumn
    @JsonIgnore
    private Medication medication;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PATIENT_ID", nullable = false)
    private UserModel patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRACTITIONER_ID", nullable = false)
    private UserModel practitioner;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime beginTime;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime endTime;

}
