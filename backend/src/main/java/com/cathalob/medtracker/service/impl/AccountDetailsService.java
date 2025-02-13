package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.factory.AccountDetailsFactory;
import com.cathalob.medtracker.model.AccountDetails;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.repository.AccountDetailsRepository;
import com.cathalob.medtracker.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountDetailsService {
    private final UserService userService;
    private final AccountDetailsFactory accountDetailsFactory;
    private final AccountDetailsRepository accountDetailsRepository;

    public AccountDetails getDetails(String username){
        UserModel userModel = userService.findByLogin(username);
        return accountDetailsFactory.newAccountDetails(userModel);
    }

    public Long updateAccountDetails(String username){
        UserModel userModel = userService.findByLogin(username);
        accountDetailsRepository.findById(userModel.getId());
        return 1L;
    }
}
