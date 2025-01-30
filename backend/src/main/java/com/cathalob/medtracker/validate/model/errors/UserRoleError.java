package com.cathalob.medtracker.validate.model.errors;

import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.validate.ValidationError;

import java.util.List;

public class UserRoleError extends ValidationError {

    public static String of(USERROLE current, List<USERROLE> allowed) {
        return String.format("User has role '%s', where only '%s' are allowed.", current,
                String.join(", ", allowed.stream().map(USERROLE::name).toList()));
    }
}
