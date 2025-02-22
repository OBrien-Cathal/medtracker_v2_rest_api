package com.cathalob.medtracker.validate.model;

import com.cathalob.medtracker.exception.validation.PrescriptionValidatorException;
import com.cathalob.medtracker.exception.validation.UserModelValidationException;
import com.cathalob.medtracker.model.prescription.Prescription;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.validate.Validator;
import com.cathalob.medtracker.validate.model.user.UserModelValidator;

import java.time.LocalDateTime;
import java.util.List;

public class PrescriptionValidator extends Validator {

    private final Prescription prescription;
    private final Prescription existingPrescription;
    private final List<Dose> existingDoses;

    public PrescriptionValidator(Prescription prescription, Prescription existingPrescription, List<Dose> existingDoses) {
        this.prescription = prescription;
        this.existingPrescription = existingPrescription;
        this.existingDoses = existingDoses;
    }

    @Override
    protected void basicValidate() {
        validatePrescription();
    }

    @Override
    protected void raiseValidationException() {
        throw new PrescriptionValidatorException(getErrors());
    }

    private void validatePrescription() {
        validateObjectPresence(prescription);
        if (isUpdate()) validatePrescriptionUpdate();
        validateMedication();
        validateExistingDoses();

        validateBeginTime();
        validateEndTime();
        validateDoseMg();
        validatePatient();
        validatePractitioner();
    }

    private void validateExistingDoses() {
        if(existingDoses != null && !existingDoses.isEmpty()){
            addError("Cannot update prescriptions that have existing user dose data");
        }
    }

    private void validatePatient() {
        if (prescription.getPatient() == null) {
            addError("Patient must exist");
        }
        try {
            UserModelValidator.PatientUserModelValidator(prescription.getPatient()).validate();
        } catch (UserModelValidationException e) {
            addErrors(e.getErrors());
        }

        if (isUpdate() && !prescription.getPatient().getId().equals(existingPrescription.getPatient().getId())) {
            addError("Cannot change the patient of an existing prescription");
        }


    }

    private void validatePractitioner() {
        if (prescription.getPractitioner() == null) {
            addError("Patient must be registered ");
        }
        try {
            UserModelValidator.PractitionerUserModelValidator(prescription.getPractitioner()).validate();
        } catch (UserModelValidationException e) {
            addErrors(e.getErrors());
        }


    }

    private boolean isUpdate() {
        return existingPrescription != null;
    }

    private void validateDoseMg() {
        if (prescription.getDoseMg() <= 0) addError("Dose (MG) must be greater than 0");
    }

    private void validateBeginTime() {
        if (prescription.getBeginTime() == null) {
            addError("Prescription start time must be specified");
        }
//        if (prescription.getBeginTime().isBefore(LocalDateTime.now()))
//            addError("Cannot update prescriptions that have already begun");
    }

    private void validateEndTime() {
        if (prescription.getEndTime() != null &&
                (prescription.getEndTime().isBefore(prescription.getBeginTime()) ||
                        prescription.getEndTime().isEqual(prescription.getBeginTime()))) {
            addError("Prescription end time must be after begin time");
        }
    }

    private void validateMedication() {
//        MedicationValidator.AMedicationValidator(prescription.getMedication(),
//                (existingPrescription != null ? existingPrescription.getMedication() : null));
        if (prescription.getMedication() == null) {
            addError("No medication found");
            return;
        }
        if (existingPrescription != null && !(existingPrescription.getMedication().equals(prescription.getMedication()))) {
            addError("Medication of existing prescription cannot be changed");
        }
    }

    private void validatePrescriptionUpdate() {
        if (!prescription.getPractitioner().equals(existingPrescription.getPractitioner())) {
            addError("Only the prescribing practitioner can update prescriptions");
        }

    }

    public static PrescriptionValidator aPrescriptionValidator(Prescription prescription, Prescription existingPrescription, List<Dose> existingDoses) {
        return new PrescriptionValidator(prescription, existingPrescription, existingDoses);
    }

}
