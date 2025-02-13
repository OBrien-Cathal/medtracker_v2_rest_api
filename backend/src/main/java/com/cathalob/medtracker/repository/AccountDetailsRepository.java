package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.AccountDetails;
import com.cathalob.medtracker.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountDetailsRepository extends JpaRepository<AccountDetails, Long> {
    List<AccountDetails> findByUserModel(UserModel userModel);

}
