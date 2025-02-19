package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.fileupload.BloodPressureFileImporter;
import com.cathalob.medtracker.fileupload.DoseFileImporter;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class BulkDataService {
    private final PrescriptionsService prescriptionsService;
    private final BloodPressureDataService bloodPressureDataService;
    private final DoseService doseService;
    private final EvaluationDataService evaluationDataService;
    private final UserService userService;


    public void importDoseFile(MultipartFile file, String username) {
        new DoseFileImporter(
                userService.findByLogin(username),
                evaluationDataService,
                prescriptionsService,
                doseService)
                .processMultipartFile(file);
    }

    public void importBloodPressureFile(MultipartFile file, String username) {
        new BloodPressureFileImporter(
                userService.findByLogin(username),
                evaluationDataService,
                bloodPressureDataService)
                .processMultipartFile(file);
    }

    public List<BloodPressureReading> getAllBloodPressureReadings(String username) {
        return bloodPressureDataService.getAllBloodPressureReadings(username).stream()
                .sorted().toList();
    }


    public List<Dose> getAllDoses(String username) {
        return doseService.getAllDoses(username).stream()
                .sorted().toList();
    }
}
