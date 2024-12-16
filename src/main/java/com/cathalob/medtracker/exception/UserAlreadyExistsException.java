package com.cathalob.medtracker.exception;

public class UserAlreadyExistsException extends InternalException {
    public UserAlreadyExistsException(String username) {
        super(expandedMessage(username));
    }

    public UserAlreadyExistsException() {
        super(expandedMessage("UNKNOWN_USER"));
    }

    public static String expandedMessage(String username) {
        return "User '" + username + "' already exists";
    }
}
