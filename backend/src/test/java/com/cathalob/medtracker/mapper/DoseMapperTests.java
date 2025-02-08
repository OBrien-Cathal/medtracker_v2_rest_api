package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.factory.DoseServiceModelFactory;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.prescription.Medication;
import com.cathalob.medtracker.model.prescription.PrescriptionScheduleEntry;

import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.payload.data.GraphData;
import com.cathalob.medtracker.testdata.*;
import com.cathalob.medtracker.testdata.service.DoseServiceTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;


import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class DoseMapperTests {
    @InjectMocks
    private DoseMapper doseMapper;

    @Test
    @DisplayName("Dose graph data removes column with no data")
    public void givenDoseMapWithEmptyColumn_whenGetDoseGraphData_thenReturnDataWithEmptyColumnRemoved() {
        //given - precondition or setup
        DoseServiceTestDataBuilder doseServiceTestDataBuilder = new DoseServiceTestDataBuilder();

        LocalDate date = LocalDate.now();
        LocalDate requestDateStart = date.plusDays(-1);
        LocalDate requestDateEnd = date.plusDays(1);


        UserModelBuilder patientBuilder = aUserModel().withRole(USERROLE.PATIENT);
        UserModel patient = patientBuilder.build();

        Medication med1 = MedicationBuilder.aMedication().withId(1L).withName("Med1").build();
        Medication med2 = MedicationBuilder.aMedication().withId(2L).withName("Med2").build();

        List<PrescriptionScheduleEntry> pseList = new ArrayList<>();
        List<DAYSTAGE> p1DayStages = List.of(DAYSTAGE.WAKEUP, DAYSTAGE.BEDTIME);
        List<DAYSTAGE> p2DayStages = List.of(DAYSTAGE.WAKEUP, DAYSTAGE.MIDDAY, DAYSTAGE.BEDTIME);

        doseServiceTestDataBuilder.addPSEs(patient, med1, 5, p1DayStages, requestDateStart, requestDateEnd, pseList);
        doseServiceTestDataBuilder.addPSEs(patient, med2, 10, p2DayStages, requestDateStart, requestDateEnd, pseList);


        HashMap<LocalDate, List<PrescriptionScheduleEntry>> entriesByDate = new HashMap<>();
        entriesByDate.put(requestDateStart,
                pseList.stream().filter(pse -> pse.getPrescription().getMedication().equals(med1)).toList());
        entriesByDate.put(date, pseList);
        entriesByDate.put(requestDateEnd,
                pseList.stream().filter(pse -> pse.getPrescription().getMedication().equals(med2)).toList());


        TreeMap<LocalDate, List<Dose>> doseMap = new TreeMap<>();
        doseMap.put(requestDateStart, entriesByDate.get(requestDateStart).stream().map(DoseServiceModelFactory::GetDummyDose).toList());
        doseMap.put(date, entriesByDate.get(date).stream().map(DoseServiceModelFactory::GetDummyDose).toList());
        doseMap.put(requestDateEnd, entriesByDate.get(requestDateEnd).stream().map(DoseServiceModelFactory::GetDummyDose).toList());

        // when - action or the behaviour that we are going test
        GraphData response = doseMapper.getDoseGraphData(doseMap);


        // then - verify the output

        System.out.println("_____________");
        System.out.println(response);
        ArrayList<String> expectedColumns = new ArrayList<>(List.of("Date"));
        expectedColumns.addAll(p1DayStages.stream().map(med1::nameWithDayStage).toList());
        expectedColumns.addAll(p2DayStages.stream().map(med2::nameWithDayStage).toList());
        System.out.println(expectedColumns);

        assertThat(response).isNotNull();

        assertThat(response.getDataRows().isEmpty()).isFalse();
        assertThat(response.getDataRows().get(0).size()).isEqualTo(expectedColumns.size());
        assertThat(response.getColumnNames()).isEqualTo(expectedColumns);

    }
    @Test
    @DisplayName("Date range with same set of valid prescriptions every day should not contain any null data")
    public void givenSameActivePrescriptionsForAllDaysOfRange_whenGetDoseGraphData_thenReturnNonNullDataForAllColumns() {
        //given - precondition or setup
        DoseServiceTestDataBuilder doseServiceTestDataBuilder = new DoseServiceTestDataBuilder();

        LocalDate date = LocalDate.now();
        LocalDate requestDateStart = date.plusDays(-1);
        LocalDate requestDateEnd = date.plusDays(1);


        UserModelBuilder patientBuilder = aUserModel().withRole(USERROLE.PATIENT);
        UserModel patient = patientBuilder.build();

        Medication med1 = MedicationBuilder.aMedication().withId(1L).withName("Med1").build();
        Medication med2 = MedicationBuilder.aMedication().withId(2L).withName("Med2").build();

        List<PrescriptionScheduleEntry> pseList = new ArrayList<>();
        List<DAYSTAGE> p1DayStages = List.of(DAYSTAGE.WAKEUP, DAYSTAGE.BEDTIME);
        List<DAYSTAGE> p2DayStages = List.of(DAYSTAGE.WAKEUP, DAYSTAGE.MIDDAY, DAYSTAGE.BEDTIME);

        doseServiceTestDataBuilder.addPSEs(patient, med1, 5, p1DayStages, requestDateStart, requestDateEnd, pseList);
        doseServiceTestDataBuilder.addPSEs(patient, med2, 10, p2DayStages, requestDateStart, requestDateEnd, pseList);


        HashMap<LocalDate, List<PrescriptionScheduleEntry>> entriesByDate = new HashMap<>();
        entriesByDate.put(requestDateStart, pseList);
        entriesByDate.put(date, pseList);
        entriesByDate.put(requestDateEnd, pseList);


        TreeMap<LocalDate, List<Dose>> doseMap = new TreeMap<>();
        doseMap.put(requestDateStart, entriesByDate.get(requestDateStart).stream().map(DoseServiceModelFactory::GetDummyDose).toList());
        doseMap.put(date, entriesByDate.get(date).stream().map(DoseServiceModelFactory::GetDummyDose).toList());
        doseMap.put(requestDateEnd, entriesByDate.get(requestDateEnd).stream().map(DoseServiceModelFactory::GetDummyDose).toList());

        // when - action or the behaviour that we are going test
        GraphData response = doseMapper.getDoseGraphData(doseMap);


        // then - verify the output


        System.out.println("_____________");
        System.out.println(response);
        ArrayList<String> expectedColumns = new ArrayList<>(List.of("Date"));
        expectedColumns.addAll(p1DayStages.stream().map(med1::nameWithDayStage).toList());
        expectedColumns.addAll(p2DayStages.stream().map(med2::nameWithDayStage).toList());
        System.out.println(expectedColumns);

        assertThat(response.getDataRows().isEmpty()).isFalse();
        assertThat(response.getColumnNames()).isEqualTo(expectedColumns);
        assertThat(response.getDataRows().stream().allMatch(objects -> objects.stream().allMatch(Objects::nonNull))).isTrue();


    }




}