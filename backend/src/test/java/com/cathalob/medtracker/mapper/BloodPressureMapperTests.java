package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.enums.DAYSTAGE;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
import com.cathalob.medtracker.payload.data.GraphData;
import com.cathalob.medtracker.testdata.BloodPressureReadingBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;


class BloodPressureMapperTests {


    @Test
    public void givenExistingReadingsForFullRange_whenGetSystoleGraphData_thenReturnValueForEveryColumnAndDate() {
        //given - precondition or setup
        TreeMap<LocalDate, List<BloodPressureReading>> columnsToValues = new TreeMap<>();
        LocalDate date = LocalDate.now();

        columnsToValues.put(date, List.of(

                BloodPressureReadingBuilder.aBloodPressureReading().withDaystage(DAYSTAGE.WAKEUP).build(),
                BloodPressureReadingBuilder.aBloodPressureReading().withDaystage(DAYSTAGE.MIDDAY).build(),
                BloodPressureReadingBuilder.aBloodPressureReading().withDaystage(DAYSTAGE.BEDTIME).build()
        ));

        columnsToValues.put(date.plusDays(1), List.of(

                BloodPressureReadingBuilder.aBloodPressureReading().withSystole(121).withDaystage(DAYSTAGE.WAKEUP).build(),
                BloodPressureReadingBuilder.aBloodPressureReading().withSystole(121).withDaystage(DAYSTAGE.MIDDAY).build(),
                BloodPressureReadingBuilder.aBloodPressureReading().withSystole(121).withDaystage(DAYSTAGE.BEDTIME).build()
        ));
        columnsToValues.put(date.plusDays(2), List.of(

                BloodPressureReadingBuilder.aBloodPressureReading().withSystole(122).withDaystage(DAYSTAGE.WAKEUP).build(),
                BloodPressureReadingBuilder.aBloodPressureReading().withSystole(122).withDaystage(DAYSTAGE.MIDDAY).build(),
                BloodPressureReadingBuilder.aBloodPressureReading().withSystole(122).withDaystage(DAYSTAGE.BEDTIME).build()
        ));

        // when - action or the behaviour that we are going test

        BloodPressureMapper bloodPressureMapper = new BloodPressureMapper();
        GraphData systoleGraphData = bloodPressureMapper.getSystoleGraphDataResponse(columnsToValues).getGraphData();

        // then - verify the output
        assertThat(systoleGraphData.getColumnNames().size()).isEqualTo(4);
        assertThat(systoleGraphData.getColumnNames()).isEqualTo(List.of("Date", "Wakeup", "Midday", "Bedtime"));
        assertThat(systoleGraphData.getDataRows().stream().allMatch(Objects::nonNull)).isTrue();


    }

}