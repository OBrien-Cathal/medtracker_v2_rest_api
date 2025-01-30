package com.cathalob.medtracker.validate;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Validator {
    private final List<String> errors;

    public Validator() {
        errors = new ArrayList<>();
    }

    public boolean isValid() {
        return errors.isEmpty();
    }
    protected void addError(String error) {
        errors.add(error);
    }
}
