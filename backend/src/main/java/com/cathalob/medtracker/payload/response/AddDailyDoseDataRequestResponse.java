package com.cathalob.medtracker.payload.response;

import com.cathalob.medtracker.payload.response.generic.Response;
import com.cathalob.medtracker.payload.response.generic.ResponseInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AddDailyDoseDataRequestResponse extends Response {
    private LocalDate date;
    private Long doseId;

    public AddDailyDoseDataRequestResponse(ResponseInfo responseInfo, LocalDate date, Long doseId) {
        super(responseInfo);
        this.date = date;
        this.doseId = doseId;
    }

    public AddDailyDoseDataRequestResponse(ResponseInfo responseInfo,LocalDate date) {
        super(responseInfo);
        this.date = date;
    }

    public static AddDailyDoseDataRequestResponse Success(LocalDate date, Long doseId) {
        return new AddDailyDoseDataRequestResponse(ResponseInfo.Success(), date, doseId);
    }

    public static AddDailyDoseDataRequestResponse Failed(LocalDate date) {
        return new AddDailyDoseDataRequestResponse(ResponseInfo.Failed(), date);
    }

    public static AddDailyDoseDataRequestResponse Failed(LocalDate date, List<String> errors) {
        return new AddDailyDoseDataRequestResponse(ResponseInfo.Failed(errors), date);
    }
}

