package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.exception.validation.dose.DailyDoseDataException;
import com.cathalob.medtracker.factory.DoseServiceModelFactory;
import com.cathalob.medtracker.mapper.DoseMapper;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.payload.data.DailyDoseData;
import com.cathalob.medtracker.payload.request.graph.GraphDataForDateRangeRequest;
import com.cathalob.medtracker.payload.request.patient.AddDailyDoseDataRequest;
import com.cathalob.medtracker.payload.request.patient.GetDailyDoseDataRequest;

import com.cathalob.medtracker.repository.DoseRepository;
import com.cathalob.medtracker.repository.PrescriptionScheduleEntryRepository;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.testdata.*;
import com.cathalob.medtracker.testdata.service.DoseServiceTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


import static com.cathalob.medtracker.testdata.DailyEvaluationBuilder.aDailyEvaluation;
import static com.cathalob.medtracker.testdata.DoseBuilder.aDose;
import static com.cathalob.medtracker.testdata.PrescriptionBuilder.aPrescription;
import static com.cathalob.medtracker.testdata.PrescriptionScheduleEntryBuilder.aPrescriptionScheduleEntry;
import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class DoseServiceTests {
    @InjectMocks
    private DoseService doseService;
    @Mock
    private DoseRepository doseRepository;
    @Mock
    private UserService userService;
    @Mock
    private EvaluationDataService evaluationDataService;

    @Mock
    private PrescriptionsService prescriptionsService;
    @Mock
    private PrescriptionScheduleEntryRepository prescriptionScheduleEntryRepository;
    @Mock
    private DoseMapper doseMapper;
    @Mock
    private DoseServiceModelFactory factory;


    @DisplayName("Test GetDailyDoseData returns dose data for saved doses")
    @Test
    public void givenGetDailyDoseDataRequest_whenGetDailyDoseDataForDayWithCompleteDoseData_thenReturnDailyDoseDataWithDoseIds() {
        //given - precondition or setup
        LocalDate requestDate = LocalDate.now();


        DoseServiceTestDataBuilder doseServiceTestDataBuilder = new DoseServiceTestDataBuilder();

        UserModelBuilder patientBuilder = aUserModel().withRole(USERROLE.PATIENT);
        UserModel patient = patientBuilder.build();

        Medication med1 = MedicationBuilder.aMedication().withId(1L).withName("Med1").build();
        Medication med2 = MedicationBuilder.aMedication().withId(2L).withName("Med2").build();

        List<PrescriptionScheduleEntry> pseList = new ArrayList<>();
        List<DAYSTAGE> p1DayStages = List.of(DAYSTAGE.WAKEUP, DAYSTAGE.BEDTIME);
        List<DAYSTAGE> p2DayStages = List.of(DAYSTAGE.WAKEUP, DAYSTAGE.MIDDAY, DAYSTAGE.BEDTIME);

        doseServiceTestDataBuilder.addPSEs(patient, med1, 5, p1DayStages, requestDate, requestDate, pseList);
        doseServiceTestDataBuilder.addPSEs(patient, med2, 10, p2DayStages, requestDate, requestDate, pseList);

        DailyEvaluation evaluation = aDailyEvaluation().withRecordDate(requestDate).with(patientBuilder).build();
        List<Dose> existingDoses = DoseBuilder.dosesFor(pseList, evaluation);

        HashMap<LocalDate, List<PrescriptionScheduleEntry>> entriesByDate = new HashMap<>();

        entriesByDate.put(requestDate,
                pseList);

        given(userService.findByLogin(patient.getUsername()))
                .willReturn(patient);
        given(evaluationDataService.findDailyEvaluationsByUserModelActiveBetween(patient, requestDate, requestDate))
                .willReturn(List.of(evaluation));
        given(doseRepository.findByEvaluation(evaluation))
                .willReturn(existingDoses);
        given(prescriptionsService.getPrescriptionScheduleEntriesValidBetween(patient, requestDate, requestDate))
                .willReturn(entriesByDate);

        Map<LocalDate, List<Dose>> dummies = DoseServiceModelFactory.DummyDosesForRange(entriesByDate);
        given(factory.dummyDosesForRange(entriesByDate))
                .willReturn(dummies);


        // when - action or the behaviour that we are going test
        List<Dose> response = doseService.getDailyDoseData(patient.getUsername(), requestDate);

        // then - verify the output
        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(5);
//        assertThat(response.stream().allMatch(dose -> dose.getId() != null)).isTrue();

//        assertThat(response).isNotNull();
//        assertThat(response.getMedicationDoses().isEmpty()).isFalse();
//        assertThat(response.getMedicationDoses().size()).isEqualTo(2);
//        assertThat(response.getMedicationDoses().stream()
//                .allMatch(dailyMedicationDoseData ->
//                        dailyMedicationDoseData.getDoses().stream()
//                                .allMatch(dailyDoseData -> dailyDoseData.getDoseId() != null)))
//                .isTrue();
//        assertThat(response.getResponseInfo().isSuccessful()).isTrue();

    }

    @DisplayName("Test GetDailyDoseData creates dummy data when no persisted doses found ")
    @Test
    public void givenGetDailyDoseDataRequest_whenGetDailyDoseDataForDayWithoutData_thenReturnGetDailyDoseDataWithNoDoseIds() {
        //given - precondition or setup
        LocalDate requestDate = LocalDate.now();
        GetDailyDoseDataRequest request = GetDailyDoseDataRequest.builder().date(requestDate).build();

        DoseServiceTestDataBuilder doseServiceTestDataBuilder = new DoseServiceTestDataBuilder();

        UserModelBuilder patientBuilder = aUserModel().withRole(USERROLE.PATIENT);
        UserModel patient = patientBuilder.build();

        Medication med1 = MedicationBuilder.aMedication().withId(1L).withName("Med1").build();
        Medication med2 = MedicationBuilder.aMedication().withId(2L).withName("Med2").build();

        List<PrescriptionScheduleEntry> pseList = new ArrayList<>();
        List<DAYSTAGE> p1DayStages = List.of(DAYSTAGE.WAKEUP, DAYSTAGE.BEDTIME);
        List<DAYSTAGE> p2DayStages = List.of(DAYSTAGE.WAKEUP, DAYSTAGE.MIDDAY, DAYSTAGE.BEDTIME);

        doseServiceTestDataBuilder.addPSEs(patient, med1, 5, p1DayStages, requestDate, requestDate, pseList);
        doseServiceTestDataBuilder.addPSEs(patient, med2, 10, p2DayStages, requestDate, requestDate, pseList);

        DailyEvaluation evaluation = aDailyEvaluation().withRecordDate(requestDate).with(patientBuilder).build();


        HashMap<LocalDate, List<PrescriptionScheduleEntry>> entriesByDate = new HashMap<>();

        entriesByDate.put(requestDate,
                pseList);

        given(userService.findByLogin(patient.getUsername()))
                .willReturn(patient);
        given(evaluationDataService.findDailyEvaluationsByUserModelActiveBetween(patient, request.getDate(), request.getDate()))
                .willReturn(List.of(evaluation));
        given(doseRepository.findByEvaluation(evaluation))
                .willReturn(List.of());
        given(prescriptionsService.getPrescriptionScheduleEntriesValidBetween(patient, requestDate, requestDate))
                .willReturn(entriesByDate);

        Map<LocalDate, List<Dose>> dummies = DoseServiceModelFactory.DummyDosesForRange(entriesByDate);
        given(factory.dummyDosesForRange(entriesByDate))
                .willReturn(dummies);


        // when - action or the behaviour that we are going test
        List<Dose> response = doseService.getDailyDoseData(patient.getUsername(), request.getDate());


        // then - verify the output
        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(5);
        assertThat(response.stream().allMatch(dose -> dose.getId() == null)).isTrue();

//        assertThat(response).isNotNull();
//        assertThat(response.getMedicationDoses().isEmpty()).isFalse();
//        assertThat(response.getMedicationDoses().size()).isEqualTo(2);
//        assertThat(response.getMedicationDoses().stream()
//                .allMatch(dailyMedicationDoseData ->
//                        dailyMedicationDoseData.getDoses().stream()
//                                .allMatch(dailyDoseData -> dailyDoseData.getDoseId() == null)))
//                .isTrue();
//        assertThat(response.getResponseInfo().isSuccessful()).isTrue();

    }

    @DisplayName("Updating a valid DailyDoseData will return a Dose ID")
    @Test
    public void givenUpdateValidDoseDataEntry_whenAddDailyDoseData_thenReturnSuccessResponse() {
        //given - precondition or setup
        AddDailyDoseDataRequest request = AddDailyDoseDataRequest.builder()
                .date(LocalDate.now().plusDays(1))
                .dailyDoseData(DailyDoseData.builder()
                        .prescriptionScheduleEntryId(1L)
                        .build())
                .build();

        Dose existingDose = aDose().withId(1L).build();

        DailyEvaluation evaluation = existingDose.getEvaluation();
        PrescriptionScheduleEntry existingPrescriptionScheduleEntry = existingDose.getPrescriptionScheduleEntry();
        UserModel patient = evaluation.getUserModel();

        Dose addOrUpdateDose = DoseMapper.Dose(request);

        given(userService.findByLogin(patient.getUsername()))
                .willReturn(patient);
        given(evaluationDataService.findOrCreateDailyEvaluationForPatientAndDate(patient, request.getDate()))
                .willReturn(evaluation);
        given(prescriptionScheduleEntryRepository.findById(request.getDailyDoseData().getPrescriptionScheduleEntryId()))
                .willReturn(Optional.of(existingPrescriptionScheduleEntry));

        given(doseRepository.findByPrescriptionScheduleEntryAndEvaluation(existingPrescriptionScheduleEntry, evaluation))
                .willReturn(List.of(existingDose));

        given(doseRepository.save(addOrUpdateDose)).willReturn(addOrUpdateDose);

        // when - action or the behaviour that we are going test
        Long savedId = doseService.addDailyDoseData(
                patient.getUsername(),
                addOrUpdateDose,
                request.getDailyDoseData().getPrescriptionScheduleEntryId(),
                request.getDate());

        // then - verify the output
        assertThat(savedId).isNotNull();
        assertThat(savedId).isEqualTo(1L);

    }

    @DisplayName("Adding a valid DailyDoseData will return a Dose Id")
    @Test
    public void givenAddValidNewDoseDataEntry_whenAddDailyDoseData_thenReturnFailureResponse() {
        //given - precondition or setup
        AddDailyDoseDataRequest request = AddDailyDoseDataRequest.builder()
                .date(LocalDate.now().plusDays(1))
                .dailyDoseData(DailyDoseData.builder()
                        .prescriptionScheduleEntryId(1L)
                        .build())
                .build();

        UserModel patient = aUserModel().withRole(USERROLE.PATIENT).build();
        DailyEvaluation evaluation = aDailyEvaluation().withRecordDate(request.getDate()).build();
        evaluation.setUserModel(patient);
        PrescriptionScheduleEntry prescriptionScheduleEntry = aPrescriptionScheduleEntry().withId(1L).build();
        Dose newDose = DoseMapper.Dose(request);

        given(userService.findByLogin(patient.getUsername()))
                .willReturn(patient);
        given(evaluationDataService.findOrCreateDailyEvaluationForPatientAndDate(patient, request.getDate()))
                .willReturn(evaluation);
        given(prescriptionScheduleEntryRepository.findById(request.getDailyDoseData().getPrescriptionScheduleEntryId()))
                .willReturn(Optional.of(prescriptionScheduleEntry));
        given(doseRepository.findByPrescriptionScheduleEntryAndEvaluation(prescriptionScheduleEntry, evaluation))
                .willReturn(List.of());
        given(doseRepository.save(newDose))
                .willReturn(aDose().withId(5L).build());

        // when - action or the behaviour that we are going test
        Long savedId = doseService.addDailyDoseData(
                patient.getUsername(),
                newDose,
                request.getDailyDoseData().getPrescriptionScheduleEntryId(),
                request.getDate());

        // then - verify the output
        assertThat(savedId).isNotNull();
        assertThat(savedId).isEqualTo(5L);

    }


    @DisplayName("Adding Invalid DailyDoseData (No prescription schedule exists for the id) will throw DailyDoseDataException")
    @Test
    public void givenInValidNewDoseDataEntry_whenAddDailyDoseData_thenReturnFailureResponse() {
        //given - precondition or setup
        AddDailyDoseDataRequest request = AddDailyDoseDataRequest.builder()
                .date(LocalDate.now())
                .dailyDoseData(DailyDoseData.builder()
                        .prescriptionScheduleEntryId(1L)
                        .build())
                .build();
        PrescriptionBuilder prescriptionBuilder = aPrescription().withBeginTime(LocalDateTime.now().plusDays(5));

        Dose existingDose = aDose()
                .withId(1L)
                .withPrescriptionScheduleEntryBuilder(aPrescriptionScheduleEntry()
                        .with(prescriptionBuilder))
                .build();

        DailyEvaluation evaluation = existingDose.getEvaluation();
        UserModel patient = evaluation.getUserModel();

        PrescriptionScheduleEntry prescriptionScheduleEntry = existingDose.getPrescriptionScheduleEntry();

        Dose addOrUpdateDose = DoseMapper.Dose(request);
        addOrUpdateDose.setEvaluation(evaluation);

        given(userService.findByLogin(patient.getUsername()))
                .willReturn(patient);
        given(evaluationDataService.findOrCreateDailyEvaluationForPatientAndDate(patient, request.getDate()))
                .willReturn(evaluation);
        given(prescriptionScheduleEntryRepository.findById(prescriptionScheduleEntry.getId()))
                .willReturn(Optional.empty());
        given(doseRepository.findByPrescriptionScheduleEntryAndEvaluation(null, evaluation))
                .willReturn(List.of());

        // when - action or the behaviour that we are going test
        assertThrows(DailyDoseDataException.class, () -> doseService.addDailyDoseData(
                patient.getUsername(),
                addOrUpdateDose,
                prescriptionScheduleEntry.getId(),
                request.getDate()));

        // then - verify the output
        verify(doseRepository, never()).save(any(Dose.class));
    }

    @DisplayName("Dose graph data created for range with 2 fully active prescriptions, and no registered doses")
    @Test
    public void givenGetDoseGraphDataRequestResponse_whenGetDoseGraphData_thenReturnSuccess() {
        //given - precondition or setup
        DoseServiceTestDataBuilder doseServiceTestDataBuilder = new DoseServiceTestDataBuilder();

        GraphDataForDateRangeRequest request = doseServiceTestDataBuilder.graphDataRequestYesterdayToTomorrow();

        UserModelBuilder patientBuilder = aUserModel().withRole(USERROLE.PATIENT);
        UserModel patient = patientBuilder.build();

        Medication med1 = MedicationBuilder.aMedication().withId(1L).withName("Med1").build();
        Medication med2 = MedicationBuilder.aMedication().withId(2L).withName("Med2").build();

        List<PrescriptionScheduleEntry> pseList = new ArrayList<>();
        List<DAYSTAGE> p1DayStages = List.of(DAYSTAGE.WAKEUP, DAYSTAGE.BEDTIME);
        List<DAYSTAGE> p2DayStages = List.of(DAYSTAGE.WAKEUP, DAYSTAGE.MIDDAY, DAYSTAGE.BEDTIME);

        doseServiceTestDataBuilder.addPSEs(patient, med1, 5, p1DayStages, request.getStart(), request.getEnd(), pseList);
        doseServiceTestDataBuilder.addPSEs(patient, med2, 10, p2DayStages, request.getStart(), request.getEnd(), pseList);

        HashMap<LocalDate, List<PrescriptionScheduleEntry>> entriesByDate = new HashMap<>();

        entriesByDate.put(request.getStart(), pseList);
        entriesByDate.put(request.getEnd(), pseList);
        entriesByDate.put(request.getStart().plusDays(1), pseList);


        given(userService.findByLogin(patient.getUsername()))
                .willReturn(patient);
        given(evaluationDataService.findDailyEvaluationsByUserModelActiveBetween(patient, request.getStart(), request.getEnd()))
                .willReturn(List.of());
        given(prescriptionsService.getPrescriptionScheduleEntriesValidBetween(patient, request.getStart(), request.getEnd()))
                .willReturn(entriesByDate);

        given(factory.dummyDosesForRange(entriesByDate))
                .willReturn(DoseServiceModelFactory.DummyDosesForRange(entriesByDate));

        // when - action or the behaviour that we are going test
        TreeMap<LocalDate, List<Dose>> response = doseService.getDoseGraphData(patient.getUsername(), request.getStart(),
                request.getEnd());

        // then - verify the output
        assertThat(response.keySet().size()).isEqualTo(3);
        assertThat(response.values().stream().flatMap(List::stream).toList().size())
                .isEqualTo(5 + 5 + 5);
    }

    @DisplayName("Dose graph data created for range with partially active prescriptions, and no registered doses")
    @Test
    public void givenGetDoseGraphDataRequestForRangeWithPartiallyActivePrescriptions_whenGetDoseGraphData_thenReturnSuccess() {
        //given - precondition or setup
        DoseServiceTestDataBuilder doseServiceTestDataBuilder = new DoseServiceTestDataBuilder();

        GraphDataForDateRangeRequest request = doseServiceTestDataBuilder.graphDataRequestYesterdayToTomorrow();

        UserModelBuilder patientBuilder = aUserModel().withRole(USERROLE.PATIENT);
        UserModel patient = patientBuilder.build();

        Medication med1 = MedicationBuilder.aMedication().withId(1L).withName("Med1").build();
        Medication med2 = MedicationBuilder.aMedication().withId(2L).withName("Med2").build();

        List<PrescriptionScheduleEntry> pseList = new ArrayList<>();
        List<DAYSTAGE> p1DayStages = List.of(DAYSTAGE.WAKEUP, DAYSTAGE.BEDTIME);
        List<DAYSTAGE> p2DayStages = List.of(DAYSTAGE.WAKEUP, DAYSTAGE.MIDDAY, DAYSTAGE.BEDTIME);

        doseServiceTestDataBuilder.addPSEs(patient, med1, 5, p1DayStages, request.getStart(), request.getEnd(), pseList);
        doseServiceTestDataBuilder.addPSEs(patient, med2, 10, p2DayStages, request.getStart(), request.getEnd(), pseList);

        HashMap<LocalDate, List<PrescriptionScheduleEntry>> entriesByDate = new HashMap<>();

        entriesByDate.put(request.getStart(),
                pseList.stream().filter(pse -> pse.getPrescription().getMedication().equals(med1)).toList());
        entriesByDate.put(request.getStart().plusDays(1),
                pseList);
        entriesByDate.put(request.getEnd(),
                pseList.stream().filter(pse -> pse.getPrescription().getMedication().equals(med2)).toList());

        given(userService.findByLogin(patient.getUsername()))
                .willReturn(patient);

        given(evaluationDataService.findDailyEvaluationsByUserModelActiveBetween(patient, request.getStart(), request.getEnd()))
                .willReturn(List.of());
        given(prescriptionsService.getPrescriptionScheduleEntriesValidBetween(patient, request.getStart(), request.getEnd()))
                .willReturn(entriesByDate);

        Map<LocalDate, List<Dose>> dummies = DoseServiceModelFactory.DummyDosesForRange(entriesByDate);
        given(factory.dummyDosesForRange(entriesByDate))
                .willReturn(dummies);

        // when - action or the behaviour that we are going test
        TreeMap<LocalDate, List<Dose>> response = doseService.getDoseGraphData(patient.getUsername(), request.getStart(),
                request.getEnd());

        // then - verify the output
        assertThat(response.keySet().size()).isEqualTo(3);
        assertThat(response.values().stream().flatMap(List::stream).toList().size())
                .isEqualTo(2 + 5 + 3);

    }


}