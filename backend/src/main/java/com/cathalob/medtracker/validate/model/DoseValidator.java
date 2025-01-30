package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.validate.Validator;

public class DoseValidator extends Validator {

    public DoseValidator() {
    }

    public void validateDoseEntry(Dose dose) {
        if (dose.getId() == null) {
            validateAddDose(dose);
        } else {
            validateUpdateDose(dose);
        }
    }

    private void validateAddDose(Dose dose) {
        validateDailyEvaluation(dose.getEvaluation());
        validatePrescriptionScheduleEntry(dose.getPrescriptionScheduleEntry());
    }

    private void validateUpdateDose(Dose dose) {
        validateDailyEvaluation(dose.getEvaluation());
        validatePrescriptionScheduleEntry(dose.getPrescriptionScheduleEntry());
    }

    private void validateDailyEvaluation(DailyEvaluation dailyEvaluation) {
        new DailyEvaluationValidator().validate(dailyEvaluation);
    }

    private void validatePrescriptionScheduleEntry(PrescriptionScheduleEntry prescriptionScheduleEntry) {
        if (prescriptionScheduleEntry == null) {
            addError("PrescriptionScheduleEntry does not exist");
        }
    }

    public static DoseValidator aDoseValidator() {
        return new DoseValidator();
    }
}
