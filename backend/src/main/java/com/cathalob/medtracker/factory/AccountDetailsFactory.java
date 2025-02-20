package com.cathalob.medtracker.factory;

import com.cathalob.medtracker.model.AccountDetails;
import com.cathalob.medtracker.model.UserModel;

public class AccountDetailsFactory {

    public AccountDetails newAccountDetails(UserModel userModel) {

        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setUserModel(userModel);
//        accountDetails.setFirstName("First name");
//        accountDetails.setSurname("Second name");

        return accountDetails;
    }

}
