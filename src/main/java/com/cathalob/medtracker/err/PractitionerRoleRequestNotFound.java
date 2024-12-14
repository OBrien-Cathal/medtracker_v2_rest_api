package com.cathalob.medtracker.err;

public class PractitionerRoleRequestNotFound extends RuntimeException{
    public PractitionerRoleRequestNotFound(String message) {
        super(message);
    }
}
