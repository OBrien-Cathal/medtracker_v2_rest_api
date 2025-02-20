package com.cathalob.medtracker.factory;

import com.cathalob.medtracker.model.SignInRecord;
import com.cathalob.medtracker.model.UserModel;

import java.time.LocalDateTime;

public class SignInRecordsServiceFactory {
    public SignInRecord signInRecord(UserModel userModel){
        SignInRecord signInRecord = new SignInRecord();

        signInRecord.setUserModel(userModel);
        signInRecord.setSignInTime(LocalDateTime.now());

        return signInRecord;

    }
}
