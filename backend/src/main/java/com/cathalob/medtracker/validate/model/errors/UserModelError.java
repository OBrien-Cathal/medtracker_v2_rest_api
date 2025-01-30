package com.cathalob.medtracker.validate.model.errors;

import com.cathalob.medtracker.validate.ValidationError;



public class UserModelError extends ValidationError {

    public static String UserNotExists() {
        return "User does not exist";
    }
}


