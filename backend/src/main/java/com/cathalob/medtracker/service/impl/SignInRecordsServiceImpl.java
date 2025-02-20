package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.factory.SignInRecordsServiceFactory;
import com.cathalob.medtracker.model.SignInRecord;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.repository.SignInRecordsRepository;
import com.cathalob.medtracker.service.SignInRecordsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class SignInRecordsServiceImpl implements SignInRecordsService {
    private final SignInRecordsRepository signInRecordsRepository;
    private final SignInRecordsServiceFactory factory;

    @Override
    public List<SignInRecord> getSignInRecords() {
        return signInRecordsRepository.findAll();
    }

    @Override
    public void addSignInRecord(UserModel userModel) {
        signInRecordsRepository.save(factory.signInRecord(userModel));
    }
}
