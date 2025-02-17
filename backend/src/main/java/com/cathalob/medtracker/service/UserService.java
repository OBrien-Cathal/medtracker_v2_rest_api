package com.cathalob.medtracker.service;

import com.cathalob.medtracker.exception.UserNotFound;
import com.cathalob.medtracker.model.UserModel;

import java.util.List;
import java.util.Optional;

public interface UserService {


    UserModel findByLogin(String login) throws UserNotFound;


    List<UserModel> findByUserModelIds(List<Long> ids);

    Optional<UserModel> findUserModelById(Long id);

    List<UserModel> getUserModels();

    List<UserModel> getPractitionerUserModels();


    boolean submitPasswordChangeRequest();
}
