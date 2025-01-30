package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.validate.Validator;

public class DailyEvaluationValidator extends Validator {

    public DailyEvaluationValidator() {}

    public void validate(DailyEvaluation dailyEvaluation) {
        validatePatient(dailyEvaluation.getUserModel());

    }

    public void validatePatient(UserModel patient) {
        UserModelValidator userModelValidator = UserModelValidator.ReferencedUserModelValidator(patient);
        userModelValidator.validatePatient();
        validateUsingSubValidator(userModelValidator);
    }
}
