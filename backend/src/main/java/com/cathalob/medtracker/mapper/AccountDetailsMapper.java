package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.AccountDetails;
import com.cathalob.medtracker.payload.data.AccountDetailsData;

public class AccountDetailsMapper {
    public AccountDetailsData accountDetailsData(AccountDetails accountDetails) {
        if (accountDetails== null) return AccountDetailsData.builder().build();

        return AccountDetailsData.builder()
                .firstName(accountDetails.getFirstName())
                .surname(accountDetails.getSurname())
                .email(accountDetails.getUserModel().getUsername())
                .userModelId(accountDetails.getUserModel().getId())
                .build();

    }
}
