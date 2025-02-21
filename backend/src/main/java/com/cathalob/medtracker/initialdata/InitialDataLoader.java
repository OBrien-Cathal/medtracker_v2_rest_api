package com.cathalob.medtracker.initialdata;

import com.cathalob.medtracker.fileupload.BloodPressureFileImporter;
import com.cathalob.medtracker.fileupload.DoseFileImporter;
import com.cathalob.medtracker.fileupload.MedicationFileImporter;
import com.cathalob.medtracker.fileupload.PrescriptionFileImporter;
import com.cathalob.medtracker.service.impl.PrescriptionsService;
import com.cathalob.medtracker.service.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;


@Slf4j
@Component
public class InitialDataLoader implements ApplicationRunner {

    private final BloodPressureDataService bloodPressureDataService;
    private final PrescriptionsService prescriptionsService;
    private final MedicationsService medicationsService;
    private final DoseService doseService;
    private final List<String> userModelNames;
    private final List<String> practitionerUsernames;

    public InitialDataLoader(
            BloodPressureDataService bloodPressureDataService,
            PrescriptionsService prescriptionsService, MedicationsService medicationsService,
            DoseService doseService) {

        this.medicationsService = medicationsService;
        this.bloodPressureDataService = bloodPressureDataService;
        this.prescriptionsService = prescriptionsService;
        this.doseService = doseService;

        userModelNames = List.of("patient1@medtracker.com");
        practitionerUsernames = List.of("doc1@medtracker.com");
    }

    @Value("${initialData.loadFromFile}")
    private boolean loadFromFile;

    @Override
    public void run(ApplicationArguments args) {
        if (!loadFromFile) return;
//        basic data from data.sql only contains one medication, if more than one is present then we should not load from file again
        if (medicationsService.getMedicationsById().containsKey(2L)) return;

        processMedicationExcelFile();
        processPrescriptionExcelFile();
        processDoseExcelFile();
        processBloodPressureReadingsExcelFile();
    }

    public void processMedicationExcelFile() {
        MedicationFileImporter medicationFileImporter = new MedicationFileImporter(practitionerUsernames.get(0), medicationsService);
        medicationFileImporter.processFileNamed("./backend/src/main/resources/initialDataFiles/medications.xlsx");

    }

    public void processPrescriptionExcelFile() {
        PrescriptionFileImporter prescriptionFileImporter = new PrescriptionFileImporter(practitionerUsernames.get(0), prescriptionsService);
        prescriptionFileImporter.processFileNamed("./backend/src/main/resources/initialDataFiles/prescriptions.xlsx");
    }


    public void processDoseExcelFile() {
        DoseFileImporter doseFileImporter = new DoseFileImporter(userModelNames.get(0), doseService);
        doseFileImporter
                .processFileNamed("./backend/src/main/resources/initialDataFiles/doses.xlsx");
    }

    public void processBloodPressureReadingsExcelFile() {
        BloodPressureFileImporter bloodPressureFileImporter = new BloodPressureFileImporter(userModelNames.get(0),
                bloodPressureDataService);
        bloodPressureFileImporter
                .processFileNamed("./backend/src/main/resources/initialDataFiles/bloodPressureReadings.xlsx");
    }
}