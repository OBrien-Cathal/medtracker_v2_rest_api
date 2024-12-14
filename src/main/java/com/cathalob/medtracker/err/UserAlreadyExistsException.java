package com.cathalob.medtracker.err;

public class UserAlreadyExistsException extends RuntimeException {
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
