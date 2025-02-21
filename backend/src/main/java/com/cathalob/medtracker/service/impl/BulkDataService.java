package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.fileupload.BloodPressureFileImporter;
import com.cathalob.medtracker.fileupload.DoseFileImporter;
import com.cathalob.medtracker.fileupload.MedicationFileImporter;
import com.cathalob.medtracker.fileupload.PrescriptionFileImporter;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.puremodel.PrescriptionDetails;
import com.cathalob.medtracker.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class BulkDataService {
    private final BloodPressureDataService bloodPressureDataService;
    private final DoseService doseService;
    private final PrescriptionsService prescriptionsService;
    private final MedicationsService medicationsService;
    private final UserService userService;
    private final MailService mailService;


    public void importDoseFile(MultipartFile file, String username) {
        new DoseFileImporter(username, doseService)
                .processMultipartFile(file);
    }

    public void importBloodPressureFile(MultipartFile file, String username) {
        new BloodPressureFileImporter(
                username,
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

    public void sendDataArchiveMailMessage(HashMap<String, ByteArrayResource> attachments, String name) throws MessagingException {
        UserModel userModel = userService.findByLogin(name);

        String subject = "Data Archive " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);

        mailService.sendEmailWithAttachments(
                userModel.getUsername(),
                subject,
                "Data Archived in the attached files",
                attachments);


    }

    public List<Medication> getAllMedications() {
        return medicationsService.getMedications();
    }

    public List<PrescriptionDetails> getPrescriptionsEnteredBy(String name) {
        return prescriptionsService.getAllPrescriptionDetailsEnteredBy(name);
    }

    public void importMedicationsFile(MultipartFile reapExcelDataFile, String name) {
        System.out.println("IMPORT MEDICATIONS");
        MedicationFileImporter medicationFileImporter = new MedicationFileImporter(name, medicationsService);
        medicationFileImporter.processMultipartFile(reapExcelDataFile);
    }

    public void importPrescriptionsFile(MultipartFile reapExcelDataFile, String name) {
        System.out.println("IMPORT Prescriptions");
        PrescriptionFileImporter prescriptionFileImporter = new PrescriptionFileImporter(name, prescriptionsService);
        prescriptionFileImporter.processMultipartFile(reapExcelDataFile);
    }
}
