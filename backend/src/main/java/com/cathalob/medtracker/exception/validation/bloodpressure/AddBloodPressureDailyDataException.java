package com.cathalob.medtracker.exception.validation.bloodpressure;

import com.cathalob.medtracker.exception.validation.ValidatorException;

import java.util.List;

public class AddBloodPressureDailyDataException extends ValidatorException {
    public AddBloodPressureDailyDataException(List<String> errors) {
        super(errors);
    }
}
