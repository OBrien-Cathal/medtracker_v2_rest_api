package com.cathalob.medtracker.err;

public class UserNotFound extends RuntimeException {

    public UserNotFound(String username) {
        super(expandedMessage(username));
    }

    public static String expandedMessage(String username) {
        return "User: '" + username + "' does not exist";
    }
}
