package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.payload.request.graph.GraphDataForDateRangeRequest;
import com.cathalob.medtracker.payload.response.TimeSeriesGraphDataResponse;
import com.cathalob.medtracker.repository.BloodPressureReadingRepository;
import com.cathalob.medtracker.repository.DailyEvaluationRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

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
    private DailyEvaluationRepository dailyEvaluationRepository;
 @Mock
 private BloodPressureReadingRepository bloodPressureReadingRepository;


    @DisplayName("BP graph data request with valid date range is successful")
    @Test
    public void givenGraphDataForDateRangeRequest_whenGetSystoleGraphData_thenReturnSuccessfulResponse() {
        //given - precondition or setup
        GraphDataForDateRangeRequest request =
                GraphDataForDateRangeRequest.builder()
                        .start(LocalDate.now().plusDays(-1))
                        .end(LocalDate.now().plusDays(1))
                        .build();
        UserModelBuilder patientBuilder = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT);
        UserModel patient = patientBuilder.build();

        BloodPressureReading bpReading = BloodPressureReadingBuilder.aBloodPressureReading().build();

        given(userService.findByLogin(patient.getUsername()))
                .willReturn(patient);
        given(dailyEvaluationRepository.findDailyEvaluationsByUserModel(patient))
                .willReturn(List.of(bpReading.getDailyEvaluation()));
        given(bloodPressureReadingRepository.findByDailyEvaluation(bpReading.getDailyEvaluation()))
                .willReturn(List.of(bpReading));
        // when - action or the behaviour that we are going test
        TimeSeriesGraphDataResponse response = bloodPressureDataService.getSystoleGraphData(patient.getUsername(), request);


        // then - verify the output
        System.out.println(response);
        assertThat(response.getGraphData().getDataRows().isEmpty()).isFalse();
        assertThat(response.getResponseInfo().isSuccessful()).isTrue();



    }
    @DisplayName("BP graph data request with missing date range returns failure response")
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
        TimeSeriesGraphDataResponse response = bloodPressureDataService.getSystoleGraphData(patient.getUsername(), request);


        // then - verify the output
        System.out.println(response);
        assertThat(response.getGraphData()).isNull();
        assertThat(response.getResponseInfo().isSuccessful()).isFalse();



    }

}