package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.exception.validation.bloodpressure.AddBloodPressureDailyDataException;
import com.cathalob.medtracker.exception.validation.bloodpressure.BloodPressureGraphDataException;
import com.cathalob.medtracker.mapper.BloodPressureMapper;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.payload.data.BloodPressureData;
import com.cathalob.medtracker.payload.request.graph.GraphDataForDateRangeRequest;
import com.cathalob.medtracker.payload.request.patient.AddDatedBloodPressureReadingRequest;
import com.cathalob.medtracker.repository.BloodPressureReadingRepository;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.testdata.BloodPressureReadingBuilder;
import com.cathalob.medtracker.testdata.UserModelBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static com.cathalob.medtracker.testdata.DailyEvaluationBuilder.aDailyEvaluation;
import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class BloodPressureDataServiceTests {
    @InjectMocks
    private BloodPressureDataService bloodPressureDataService;
    @Mock
    private UserService userService;

    @Mock
    private PatientRegistrationRepository patientRegistrationRepository;

    @Mock
    private EvaluationDataService evaluationDataService;
    @Mock
    private BloodPressureReadingRepository bloodPressureReadingRepository;

    private void bpReadingsForRange(List<DAYSTAGE> dayStages,
                                    LocalDate begin, LocalDate end, List<BloodPressureReading> bpReadings) {


        LocalDate current = begin;
        while (current.isEqual(end) || current.isBefore(end)) {

            DailyEvaluation dailyEvaluation = aDailyEvaluation().withRecordDate(current).build();
            for (DAYSTAGE ds : dayStages) {
                long readingID = bpReadings.size() + 1;
                BloodPressureReading reading = BloodPressureReadingBuilder.aBloodPressureReading()
                        .withId(readingID)
                        .withDaystage(ds)
                        .build();

                reading.setDailyEvaluation(dailyEvaluation);
                bpReadings.add(reading);
            }
            current = current.plusDays(1);
        }
    }

    @DisplayName("BP graph data request with valid date range is successful")
    @Test
    public void givenGraphDataForDateRangeRequestTodayPlus3_whenGetSystoleGraphData_thenReturnSuccessfulResponse() {
        //given - precondition or setup
        LocalDate today = LocalDate.now();
        GraphDataForDateRangeRequest request =
                GraphDataForDateRangeRequest.builder()
                        .start(today.plusDays(-1))
                        .end(today.plusDays(1))
                        .build();
        UserModelBuilder patientBuilder = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT);
        UserModel patient = patientBuilder.build();

        ArrayList<BloodPressureReading> readings = new ArrayList<>();

        bpReadingsForRange(List.of(DAYSTAGE.WAKEUP, DAYSTAGE.BEDTIME), request.getStart(), request.getEnd(), readings);
        bpReadingsForRange(List.of(DAYSTAGE.WAKEUP, DAYSTAGE.MIDDAY, DAYSTAGE.BEDTIME), request.getStart(), request.getEnd(), readings);

        List<DailyEvaluation> dailyEvaluations = readings.stream().map(BloodPressureReading::getDailyEvaluation).distinct().toList();


        given(userService.findByLogin(patient.getUsername()))
                .willReturn(patient);
        given(evaluationDataService.findDailyEvaluationsByUserModelActiveBetween(patient, request.getStart(), request.getEnd()))
                .willReturn(dailyEvaluations);

        List<LocalDate> evalDates = dailyEvaluations.stream().map(DailyEvaluation::getRecordDate).toList();
        List<Long> evalUserModelIds = dailyEvaluations.stream().map(dailyEvaluation -> dailyEvaluation.getUserModel().getId()).toList();


        given(bloodPressureReadingRepository.findByDailyEvaluationDatesAndIds(evalDates, evalUserModelIds))
                .willReturn(readings);


        // when - action or the behaviour that we are going test
        TreeMap<LocalDate, List<BloodPressureReading>> map = bloodPressureDataService.getBloodPressureReadingsForDateRange(
                patient.getUsername(), request.getStart(), request.getEnd());


        // then - verify the output
        map.forEach((date, bloodPressureReadings) -> {
            System.out.println(date + "==" );
            bloodPressureReadings.forEach(System.out::println);
        });

        assertThat(map.isEmpty()).isFalse();
        assertThat(map.size()).isEqualTo(3);
        assertThat(map.get(today).size()).isEqualTo(15);


    }

    @DisplayName("BP graph data request with missing date range throws exception")
    @Test
    public void givenGraphDataForDateRangeRequestWithEmptyRange_whenGetSystoleGraphData_thenReturnFailureResponse() {
        //given - precondition or setup
        GraphDataForDateRangeRequest request =
                GraphDataForDateRangeRequest.builder()
                        .build();
        UserModelBuilder patientBuilder = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT);
        UserModel patient = patientBuilder.build();

        given(userService.findByLogin(patient.getUsername()))
                .willReturn(patient);

        // when - action or the behaviour that we are going test
        assertThrows(BloodPressureGraphDataException.class, () ->
                bloodPressureDataService.getBloodPressureReadingsForDateRange(patient.getUsername(),
                        request.getStart(),
                        request.getEnd()));
        // then - verify the output
        verify(bloodPressureReadingRepository, never()).save(any(BloodPressureReading.class));
    }

    @DisplayName("Adding a valid BloodPressureData will return a BloodPressureReading Id")
    @Test
    public void givenAddValidNewBloodPressureDataEntry_whenAddDailyData_thenReturnSavedId() {
        //given - precondition or setup
        AddDatedBloodPressureReadingRequest request = AddDatedBloodPressureReadingRequest.builder()
                .date(LocalDate.now().plusDays(1))
                .data(BloodPressureData.builder()
                        .systole(120)
                        .build())
                .build();

        UserModel patient = aUserModel().withRole(USERROLE.PATIENT).build();
        DailyEvaluation evaluation = aDailyEvaluation().withRecordDate(request.getDate()).build();
        evaluation.setUserModel(patient);

        BloodPressureReading newReading = BloodPressureMapper.ToBloodPressureReading(request);

        given(userService.findByLogin(patient.getUsername()))
                .willReturn(patient);
        given(evaluationDataService.findOrCreateDailyEvaluationForPatientAndDate(patient, request.getDate()))
                .willReturn(evaluation);
        given(bloodPressureReadingRepository.save(newReading))
                .willReturn(BloodPressureReadingBuilder.aBloodPressureReading().withId(5L).build());

        // when - action or the behaviour that we are going test
        Long savedId = bloodPressureDataService.addBloodPressureReading(
                newReading,
                request.getDate(),
                patient.getUsername());

        // then - verify the output
        assertThat(savedId).isNotNull();
        assertThat(savedId).isEqualTo(5L);

    }

    @DisplayName("Adding an Invalid BloodPressureData will throw an exception")
    @Test
    public void givenAddInvalidNewBloodPressureDataEntry_whenAddDailyData_thenThrowException() {
        //given - precondition or setup
        AddDatedBloodPressureReadingRequest request = AddDatedBloodPressureReadingRequest.builder()
                .date(LocalDate.now().plusDays(1))
                .data(BloodPressureData.builder()
                        .systole(120)
                        .build())
                .build();

        UserModel notAPatient = aUserModel().build();
        DailyEvaluation evaluation = aDailyEvaluation().withRecordDate(request.getDate()).build();
        evaluation.setUserModel(notAPatient);

        BloodPressureReading newReading = BloodPressureMapper.ToBloodPressureReading(request);

        given(userService.findByLogin(notAPatient.getUsername()))
                .willReturn(notAPatient);

        // when - action or the behaviour that we are going test
        assertThrows(AddBloodPressureDailyDataException.class, () ->
                bloodPressureDataService.addBloodPressureReading(
                        newReading,
                        request.getDate(),
                        notAPatient.getUsername()));

        // then - verify the output
        verify(bloodPressureReadingRepository, never()).save(any(BloodPressureReading.class));


    }


}