package com.cathalob.medtracker.model.tracking;
import com.cathalob.medtracker.model.UserModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity(name = "DAILYEVALUATION")
@Data
@IdClass(DailyEvaluationId.class)
@AllArgsConstructor
@NoArgsConstructor
public class DailyEvaluation {

    @Id
    private LocalDate recordDate;
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USERMODEL_ID", nullable = false)
    @JsonIgnore
    private UserModel userModel;



    public DailyEvaluationId getDailyEvaluationIdClass() {
        return new DailyEvaluationId(userModel.getId(), recordDate);
    }
}
