package com.cathalob.medtracker.exception;

import com.cathalob.medtracker.exception.validation.ValidatorException;

import java.util.List;

public class UserNotFound extends ValidatorException {

    public UserNotFound(String username) {
        super(List.of(expandedMessage(username)));
    }

    public static String expandedMessage(String username) {
        return "User: '" + username + "' does not exist";
    }
}
