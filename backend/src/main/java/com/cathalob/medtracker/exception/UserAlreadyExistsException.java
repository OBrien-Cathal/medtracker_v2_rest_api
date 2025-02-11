package com.cathalob.medtracker.exception;

import com.cathalob.medtracker.exception.validation.ValidatorException;

import java.util.List;

public class UserAlreadyExistsException extends ValidatorException {

    public UserAlreadyExistsException(String username) {

        super(List.of(expandedMessage(username)));
    }

    public UserAlreadyExistsException() {

        super(List.of(expandedMessage("UNKNOWN_USER")));
    }

    public static String expandedMessage(String username) {
        return "User '" + username + "' already exists";
    }
}
