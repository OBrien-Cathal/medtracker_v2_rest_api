package com.cathalob.medtracker.exception;

import com.cathalob.medtracker.exception.validation.ValidatorException;

import java.util.List;

public class UserAuthenticationValidatorException extends ValidatorException {
    public UserAuthenticationValidatorException(List<String> errors) {
        super(errors);
    }
}
