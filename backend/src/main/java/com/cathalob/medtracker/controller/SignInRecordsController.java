package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.mapper.SignInRecordsMapper;
import com.cathalob.medtracker.payload.data.SignInRecordData;
import com.cathalob.medtracker.service.SignInRecordsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequestMapping("/api/v1/sign-in-records")
@RequiredArgsConstructor
@RestController
public class SignInRecordsController {
    private final SignInRecordsService signInRecordService;
    private final SignInRecordsMapper mapper;

    @GetMapping
    public ResponseEntity<List<SignInRecordData>> getSignInRecords() {

        return ResponseEntity.ok(mapper.signInRecordDataList(signInRecordService.getSignInRecords()));
    }
}
