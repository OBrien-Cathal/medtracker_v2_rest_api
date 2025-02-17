package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserModel>> getUserModels() {
        return ResponseEntity.ok(userService.getUserModels());
    }


    @GetMapping("/practitioners")
    public ResponseEntity<List<UserModel>> getPractitionerUserModels() {
        return ResponseEntity.ok(userService.getPractitionerUserModels());
    }

}
