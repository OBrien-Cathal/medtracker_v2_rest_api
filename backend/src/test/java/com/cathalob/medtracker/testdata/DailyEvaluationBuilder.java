package com.cathalob.medtracker.testdata;

import com.cathalob.medtracker.model.tracking.DailyEvaluation;

import java.time.LocalDate;

public class DailyEvaluationBuilder {
    public DailyEvaluationBuilder withRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
        return this;
    }

    public DailyEvaluationBuilder with(UserModelBuilder userModelBuilder) {
        this.userModelBuilder = userModelBuilder;
        return this;
    }

    private LocalDate recordDate = LocalDate.now();
    private UserModelBuilder userModelBuilder = new UserModelBuilder();

    public DailyEvaluationBuilder(DailyEvaluationBuilder copy) {
        this.recordDate = copy.recordDate;
        this.userModelBuilder = copy.userModelBuilder;
    }

    public DailyEvaluationBuilder() {
    }

    public DailyEvaluationBuilder but() {
        return new DailyEvaluationBuilder(this);
    }

    public static DailyEvaluationBuilder aDailyEvaluation() {
        return new DailyEvaluationBuilder();
    }

    public DailyEvaluation build() {
        return new DailyEvaluation(recordDate, userModelBuilder.build());
    }

}
