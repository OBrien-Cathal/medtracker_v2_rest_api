package com.cathalob.medtracker.model.prescription;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Entity(name="PRESCRIPTIONSCHEDULEENTRY")
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionScheduleEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRESCRIPTION_ID", nullable = false)
    private Prescription prescription;
    @Enumerated(EnumType.STRING)
    private DAYSTAGE dayStage;

}
