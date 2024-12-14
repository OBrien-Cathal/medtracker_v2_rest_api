package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.repository.UserModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserControllerApi {
    private final UserModelRepository userModelRepository;
    @GetMapping
    public ResponseEntity<List<UserModel>> getUserModels(){
        return ResponseEntity.ok(userModelRepository.findAll());
    }
}
