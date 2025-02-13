package com.cathalob.medtracker.factory;

import com.cathalob.medtracker.model.AccountRegistration;
import com.cathalob.medtracker.model.UserModel;

import java.time.LocalDateTime;
import java.util.UUID;

public class AccountRegistrationFactory {

    public AccountRegistration newAccountRegistration(UserModel userModel){

        AccountRegistration accountRegistration = new AccountRegistration();
        accountRegistration.setUserModel(userModel);
        accountRegistration.setConfirmed(false);
        accountRegistration.setRegistrationId(UUID.randomUUID());
        accountRegistration.setRegistrationTime(LocalDateTime.now());

        return accountRegistration;

    }

}
