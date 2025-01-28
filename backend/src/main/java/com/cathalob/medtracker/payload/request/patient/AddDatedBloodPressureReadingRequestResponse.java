package com.cathalob.medtracker.payload.request.patient;

import com.cathalob.medtracker.payload.response.generic.Response;
import com.cathalob.medtracker.payload.response.generic.ResponseInfo;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
public class AddDatedBloodPressureReadingRequestResponse extends Response {
    private LocalDate date;
    private Long bloodPressureReadingId;
    public AddDatedBloodPressureReadingRequestResponse(ResponseInfo responseInfo, Long bloodPressureReadingId) {
        super(responseInfo);
        this.bloodPressureReadingId = bloodPressureReadingId;
    }

    public AddDatedBloodPressureReadingRequestResponse(ResponseInfo responseInfo) {
        super(responseInfo);
    }

    public static AddDatedBloodPressureReadingRequestResponse Success(Long bloodPressureReadingId) {
        return new AddDatedBloodPressureReadingRequestResponse(ResponseInfo.Success(), bloodPressureReadingId);
    }

    public static AddDatedBloodPressureReadingRequestResponse Failed( List<String> errors) {
        return new AddDatedBloodPressureReadingRequestResponse(ResponseInfo.Failed(errors));
    }
}
