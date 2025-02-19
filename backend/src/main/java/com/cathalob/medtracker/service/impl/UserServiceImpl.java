package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.exception.UserNotFound;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.repository.UserModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
@EnableMethodSecurity
public class UserServiceImpl implements com.cathalob.medtracker.service.UserService {
    private final UserModelRepository userModelRepository;

    @Override
    public UserModel findByLogin(String username) throws UserNotFound {
        Optional<UserModel> maybeUserModel = userModelRepository.findByUsername(username);
        if (maybeUserModel.isEmpty()) throw new UserNotFound(username);
        return maybeUserModel.orElse(null);
    }

    @Override
    public List<UserModel> findByUserModelIds(List<Long> ids) {
        return userModelRepository.findAll();
    }

    @Override
    public Optional<UserModel> findUserModelById(Long id) {
        return userModelRepository.findById(id);
    }

    @Override
    public List<UserModel> getUserModels() {
        return findByUserModelIds(List.of());
    }


    @Override
    public List<UserModel> getPractitionerUserModels() {
        return userModelRepository.findByRole(USERROLE.PRACTITIONER);
    }

    public Map<Long, UserModel> getUserModelsById() {
        return getUserModels()
                .stream().collect(Collectors.toMap(UserModel::getId, Function.identity()));
    }

    @Override
    public UserModel saveUserModel(UserModel userModel) {
        return userModelRepository.save(userModel);
    }


    @Override
    public boolean submitPasswordChangeRequest() {
        return false;
    }
}
