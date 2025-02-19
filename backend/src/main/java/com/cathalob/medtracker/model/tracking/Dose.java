package com.cathalob.medtracker.model.tracking;

import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "DOSE")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dose implements Comparable<Dose> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "DAILYEVALUATION_RECORD_DATE")
    @JoinColumn(name = "DAILYEVALUATION_USERMODEL_ID")
    private DailyEvaluation evaluation;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime doseTime;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRESCRIPTION_SCHEDULE_ENTRY_ID", nullable = false)

    private PrescriptionScheduleEntry prescriptionScheduleEntry;

    private boolean taken;

    @Override
    public int compareTo(Dose o) {

        if (!this.getEvaluation().getRecordDate().isEqual(o.getEvaluation().getRecordDate())) {
            return this.getEvaluation().getRecordDate().compareTo(o.getEvaluation().getRecordDate());
        }

        int myDsOrdinal = this.getPrescriptionScheduleEntry().getDayStage().ordinal();
        int otherDsOrdinal = o.getPrescriptionScheduleEntry().getDayStage().ordinal();

        if (myDsOrdinal != otherDsOrdinal) {
            return myDsOrdinal - otherDsOrdinal;
        }

        return this.getDoseTime().toLocalTime().compareTo(o.getDoseTime().toLocalTime());

    }
}
