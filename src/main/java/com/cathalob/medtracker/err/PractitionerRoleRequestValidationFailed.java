package com.cathalob.medtracker.err;

public class PractitionerRoleRequestValidationFailed extends RuntimeException{
    public PractitionerRoleRequestValidationFailed(String message) {
        super(message);
    }
}
