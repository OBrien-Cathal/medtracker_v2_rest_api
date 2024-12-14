package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.dto.UserModelDTO;
import com.cathalob.medtracker.model.UserModel;

public class UserModelMapper {

    public static UserModelDTO asDTO(UserModel userModel){
        return UserModelDTO.builder()
                .password(userModel.getPassword())
                .username(userModel.getUsername()).build();

    }

    public static UserModel fromDTO(UserModelDTO userModelDTO){
        UserModel userModel = new UserModel();
        userModel.setUsername(userModelDTO.getUsername());
        userModel.setPassword(userModelDTO.getPassword());
        return userModel;
    }
}
