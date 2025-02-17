package com.cathalob.medtracker.exception.validation;

import java.util.List;

public class SubmitRoleChangeValidatorException extends ValidatorException{
    public SubmitRoleChangeValidatorException(List<String> errors) {
        super(errors);
    }
}
