package com.cathalob.medtracker.model.tracking;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity(name = "BLOODPRESSUREREADING")
@Data
public class BloodPressureReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DAYSTAGE dayStage;

    @ManyToOne
    @JoinColumn(name = "DAILYEVALUATION_RECORD_DATE", nullable = false)
    @JoinColumn(name = "DAILYEVALUATION_USERMODEL_ID", nullable = false)
    @JsonIgnore
    private DailyEvaluation dailyEvaluation;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime readingTime;

    private Integer systole;
    private Integer diastole;
    private Integer heartRate;

    public boolean hasData() {
        return systole != null && diastole != null && heartRate != null;
    }
}