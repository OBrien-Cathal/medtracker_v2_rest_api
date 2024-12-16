package com.cathalob.medtracker.exception;

public class ExternalException extends MedTrackerException {
    public ExternalException(InternalException internalException) {
        super(getErrorMessageWithCode(internalException));
    }

    public ExternalException(String message, InternalException originalException) {
        super(getErrorMessageWithCode(message, originalException.getErrorCode()));
    }

    public static String getErrorMessageWithCode(InternalException exception) {
        return "Error: " + exception.getMessage() + " Code: " + exception.getErrorCode();

    }
    public static String getErrorMessageWithCode(String message, Integer exceptionCode) {
        return "Error: " + message + " Code: " + exceptionCode;

    }
}
