package com.cathalob.medtracker.exception.validation.medication;

import com.cathalob.medtracker.exception.validation.ValidationException;

public class MedicationValidationException extends ValidationException {
    public MedicationValidationException(String message) {
        super(message);
    }
}
