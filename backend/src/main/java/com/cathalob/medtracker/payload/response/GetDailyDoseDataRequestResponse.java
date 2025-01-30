package com.cathalob.medtracker.payload.response;

import com.cathalob.medtracker.model.tracking.Dose;
import com.cathalob.medtracker.payload.data.DailyMedicationDoseData;
import com.cathalob.medtracker.payload.response.generic.Response;
import com.cathalob.medtracker.payload.response.generic.ResponseInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetDailyDoseDataRequestResponse extends Response {
    private LocalDate date;
    private List<DailyMedicationDoseData> medicationDoses;

    public GetDailyDoseDataRequestResponse(ResponseInfo responseInfo, LocalDate date, List<DailyMedicationDoseData> medicationDoses) {
        super(responseInfo);
        this.date = date;
        this.medicationDoses = medicationDoses;
    }

    public GetDailyDoseDataRequestResponse(ResponseInfo responseInfo, LocalDate date) {
        super(responseInfo);
        this.date = date;
    }


    public static GetDailyDoseDataRequestResponse Success(LocalDate date, List<DailyMedicationDoseData> medicationDoses) {
        return new GetDailyDoseDataRequestResponse(ResponseInfo.Success(), date, medicationDoses);
    }

    public static GetDailyDoseDataRequestResponse Failed(LocalDate date) {
        return new GetDailyDoseDataRequestResponse(ResponseInfo.Failed(), date);
    }

    public static GetDailyDoseDataRequestResponse Failed(LocalDate date, List<String> errors) {
        return new GetDailyDoseDataRequestResponse(ResponseInfo.Failed(errors), date);
    }


}
