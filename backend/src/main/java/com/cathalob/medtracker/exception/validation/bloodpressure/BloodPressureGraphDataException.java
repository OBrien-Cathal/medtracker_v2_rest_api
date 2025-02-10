package com.cathalob.medtracker.exception.validation.bloodpressure;

import com.cathalob.medtracker.exception.validation.ValidatorException;

import java.util.List;

public class BloodPressureGraphDataException extends ValidatorException {
    public BloodPressureGraphDataException(List<String> errors) {
        super(errors);
    }
}
