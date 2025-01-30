package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;
import com.cathalob.medtracker.model.tracking.DailyEvaluation;
import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.payload.request.patient.GetDailyDoseDataRequest;
import com.cathalob.medtracker.payload.response.GetDailyDoseDataRequestResponse;
import com.cathalob.medtracker.repository.DailyEvaluationRepository;
import com.cathalob.medtracker.repository.DoseRepository;
import com.cathalob.medtracker.repository.PrescriptionScheduleEntryRepository;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.testdata.*;
import com.cathalob.medtracker.validate.model.UserRoleValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.cathalob.medtracker.testdata.DailyEvaluationBuilder.aDailyEvaluation;
import static com.cathalob.medtracker.testdata.DoseBuilder.aDose;
import static com.cathalob.medtracker.testdata.PrescriptionBuilder.aPrescription;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

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
    private DailyEvaluationRepository dailyEvaluationRepository;

    @Mock
    private PrescriptionsService prescriptionsService;

    @Mock
    private PrescriptionScheduleEntryRepository prescriptionScheduleEntryRepository;


    private List<Dose> buildDosesList(List<String> medicationNames, List<DAYSTAGE> daystageList) {
        UserModelBuilder userModelBuilder = UserModelBuilder.aUserModel().withRole(USERROLE.PATIENT);

        DailyEvaluationBuilder dailyEvaluationBuilder = aDailyEvaluation()
                .with(userModelBuilder);

        List<PrescriptionBuilder> prescriptionBuilders = Stream.iterate(0, n -> n + 1)
                .limit(medicationNames.size())
                .map(medIndex -> {
                            Long id = (long) (medIndex + 1);
                            return aPrescription()
                                    .withPatient(userModelBuilder)
                                    .withId(id)
                                    .with(MedicationBuilder.aMedication()
                                            .withId(id)
                                            .withName(medicationNames.get(medIndex)))
                                    .withEndTime(LocalDateTime.now().plusDays(5));
                        }
                ).toList();

        return Stream.iterate(0, p -> p + 1)
                .limit(prescriptionBuilders.size()).flatMap(
                        pIndex -> Stream.iterate(0, n -> n + 1)
                                .limit(daystageList.size())
                                .map(index -> {
                                    long id = ((long) daystageList.size() * pIndex) + (index + 1);

                                    PrescriptionScheduleEntryBuilder prescriptionScheduleEntryBuilder = PrescriptionScheduleEntryBuilder
                                            .aPrescriptionScheduleEntry().withId(id)
                                            .with(prescriptionBuilders.get(pIndex));
                                    return aDose()
                                            .withId(id)
                                            .withDailyEvaluationBuilder(dailyEvaluationBuilder)
                                            .withPrescriptionScheduleEntryBuilder(prescriptionScheduleEntryBuilder)
                                            .build();

                                })).toList();

    }


    @DisplayName("When Doses are registered for the full set of prescription schedules, return DailyDoseData with Dose Ids")
    @Test
    public void givenGetDailyDoseDataRequestResponse_whenGetDailyDoseDataForDayWithCompleteDoseData_thenReturnGetDailyDoseDataResponseWithDoseData() {
        //given - precondition or setup
        GetDailyDoseDataRequest request = GetDailyDoseDataRequest.builder().date(LocalDate.now()).build();

        List<Dose> doseList = buildDosesList(List.of("Med1", "Med2"), List.of(DAYSTAGE.WAKEUP, DAYSTAGE.BEDTIME));
        Dose dose = doseList.stream().findAny().orElse(null);
        System.out.println(doseList);
        assertThat(dose).isNotNull();

        DailyEvaluation evaluation = dose.getEvaluation();
        UserModel patient = evaluation.getUserModel();
        given(userService.findByLogin(patient.getUsername())).willReturn(patient);
        given(doseRepository.findByEvaluation(evaluation)).willReturn(doseList);


        given(prescriptionsService.getPrescriptionsValidOnDate(patient, request.getDate()))
                .willReturn(List.of(dose.getPrescriptionScheduleEntry().getPrescription()));
        given(prescriptionScheduleEntryRepository.findByPrescription(dose.getPrescriptionScheduleEntry().getPrescription()))
                .willReturn(doseList
                        .stream()
                        .map(Dose::getPrescriptionScheduleEntry).toList());
        given(dailyEvaluationRepository.findById(evaluation.getDailyEvaluationIdClass())).willReturn(Optional.of(evaluation));

        // when - action or the behaviour that we are going test
        GetDailyDoseDataRequestResponse response = doseService.getDailyDoseData(request, patient.getUsername());
        System.out.println(response);

        // then - verify the output
        assertThat(response).isNotNull();
        assertThat(response.getMedicationDoses().isEmpty()).isFalse();
        assertThat(response.getMedicationDoses().stream()
                .allMatch(dailyMedicationDoseData ->
                        dailyMedicationDoseData.getDoses().stream()
                                .allMatch(dailyDoseData -> dailyDoseData.getDoseId() != null)))
                .isTrue();
        assertThat(response.getResponseInfo().isSuccessful()).isTrue();

    }

    @DisplayName("When no doses are registered for the full set of prescription schedules, return DailyDoseData with Empty Dose Ids")
    @Test
    public void givenGetDailyDoseDataRequestResponse_whenGetDailyDoseDataForNewDay_thenReturnGetDailyDoseDataResponseWithDoseData() {
        //given - precondition or setup
        GetDailyDoseDataRequest request = GetDailyDoseDataRequest.builder().date(LocalDate.now()).build();
        List<Dose> doseList = buildDosesList(List.of("Med1", "Med2"), List.of(DAYSTAGE.WAKEUP, DAYSTAGE.BEDTIME));
        Dose dose = doseList.stream().findAny().orElse(null);
        assertThat(dose).isNotNull();

        DailyEvaluation evaluation = dose.getEvaluation();
        UserModel patient = evaluation.getUserModel();
        given(userService.findByLogin(patient.getUsername())).willReturn(patient);
        given(doseRepository.findByEvaluation(evaluation)).willReturn(List.of());

        given(prescriptionsService.getPrescriptionsValidOnDate(patient, request.getDate()))
                .willReturn(List.of(dose.getPrescriptionScheduleEntry().getPrescription()));
        given(prescriptionScheduleEntryRepository.findByPrescription(dose.getPrescriptionScheduleEntry().getPrescription()))
                .willReturn(doseList
                        .stream()
                        .map(Dose::getPrescriptionScheduleEntry).toList());
        given(dailyEvaluationRepository.findById(evaluation.getDailyEvaluationIdClass())).willReturn(Optional.of(evaluation));

        // when - action or the behaviour that we are going test
        GetDailyDoseDataRequestResponse response = doseService.getDailyDoseData(request, patient.getUsername());
        System.out.println(response);

        // then - verify the output
        assertThat(response).isNotNull();
        assertThat(response.getMedicationDoses().isEmpty()).isFalse();
        assertThat(response.getMedicationDoses().stream()
                .allMatch(dailyMedicationDoseData ->
                        dailyMedicationDoseData.getDoses().stream()
                                .allMatch(dailyDoseData -> dailyDoseData.getDoseId() == null)))
                .isTrue();
        assertThat(response.getResponseInfo().isSuccessful()).isTrue();

    }

}