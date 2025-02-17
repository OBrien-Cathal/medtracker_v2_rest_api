package com.cathalob.medtracker.exception.validation;

import java.util.List;

public class ApproveRoleChangeValidationException extends ValidatorException{
    public ApproveRoleChangeValidationException(List<String> errors) {
        super(errors);
    }
}
