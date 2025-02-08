package com.cathalob.medtracker.exception.validation.dose;

import com.cathalob.medtracker.exception.validation.ValidatorException;

import java.util.List;

public class DoseGraphDataException extends ValidatorException {
    public DoseGraphDataException(List<String> errors) {
        super(errors);
    }
}
