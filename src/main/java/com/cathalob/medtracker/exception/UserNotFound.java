package com.cathalob.medtracker.exception;

public class UserNotFound extends InternalException {

    public UserNotFound(String username) {
        super(expandedMessage(username));
    }

    public static String expandedMessage(String username) {
        return "User: '" + username + "' does not exist";
    }
}
