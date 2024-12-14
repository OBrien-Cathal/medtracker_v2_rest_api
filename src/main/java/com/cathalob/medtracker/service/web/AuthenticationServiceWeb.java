package com.cathalob.medtracker.service.web;

import com.cathalob.medtracker.dto.UserModelDTO;
import com.cathalob.medtracker.err.UserAlreadyExistsException;
import com.cathalob.medtracker.err.UserNotFound;
import com.cathalob.medtracker.mapper.UserModelMapper;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.repository.UserModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor

public class AuthenticationServiceWeb {
    private final PasswordEncoder passwordEncoder;
    private final UserModelRepository userModelRepository;


    public void register(UserModelDTO userModelDTO) {
        UserModelMapper.asDTO(register(UserModelMapper.fromDTO(userModelDTO)));
    }

    public UserModel register(UserModel userModel) throws UserAlreadyExistsException {
        if (userModelRepository.findByUsername(userModel.getUsername())
                .isPresent()) {
            throw new UserAlreadyExistsException(userModel.getUsername());
        }

        userModel.setRole(USERROLE.USER);
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        return userModelRepository.save(userModel);
    }
    public UserModel findByLogin(String username) throws UserNotFound {
        Optional<UserModel> maybeUserModel = userModelRepository.findByUsername(username);
        if (maybeUserModel.isEmpty()) throw new UserNotFound(username);
        return maybeUserModel.orElse(null);
    }

}
