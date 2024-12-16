package com.cathalob.medtracker.exception;

import java.util.Arrays;

public class MedTrackerException extends RuntimeException {
    public MedTrackerException(String message) {
    }

    public Integer getErrorCode() {
        return errorCode();
    }
    private static Integer errorCode(){return -1;}
}
