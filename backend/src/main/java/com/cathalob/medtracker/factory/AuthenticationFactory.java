package com.cathalob.medtracker.factory;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;

public class AuthenticationFactory {

    public UserModel signUpUserModel(String lowerCaseUsername, String password) {


        return UserModel.builder()
                .username(lowerCaseUsername)
                .password(password)
                .role(USERROLE.USER)
                .build();
    }

}
