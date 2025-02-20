package com.cathalob.medtracker.service;

import com.cathalob.medtracker.model.SignInRecord;
import com.cathalob.medtracker.model.UserModel;

import java.util.List;

public interface SignInRecordsService {

    abstract List<SignInRecord> getSignInRecords();

    abstract void addSignInRecord(UserModel userModel);

}
