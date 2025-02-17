package com.cathalob.medtracker.exception.validation;

import java.util.List;

public class ApprovePatientRegistrationValidatorException extends ValidatorException{

    public ApprovePatientRegistrationValidatorException(List<String> errors) {
        super(errors);
    }
}
