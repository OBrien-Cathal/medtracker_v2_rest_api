package com.cathalob.medtracker.exception.validation.bloodpressure;

import com.cathalob.medtracker.exception.validation.ValidatorException;

import java.util.List;

public class BloodPressureDailyDataExceptionData extends ValidatorException {


    public BloodPressureDailyDataExceptionData(List<String> errors) {
        super(errors);
    }
}
