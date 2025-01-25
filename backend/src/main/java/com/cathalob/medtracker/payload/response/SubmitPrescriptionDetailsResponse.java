package com.cathalob.medtracker.payload.response;

import com.cathalob.medtracker.payload.response.generic.Response2;
import com.cathalob.medtracker.payload.response.generic.ResponseInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)

public class SubmitPrescriptionDetailsResponse extends Response2 {
    private Long prescriptionId;

    public SubmitPrescriptionDetailsResponse() {
        super();
    }

    public static SubmitPrescriptionDetailsResponse Failed(List<String> errors) {
        SubmitPrescriptionDetailsResponse submitPrescriptionDetailsResponse = new SubmitPrescriptionDetailsResponse();
        submitPrescriptionDetailsResponse.responseInfo.setErrors(errors);
        return submitPrescriptionDetailsResponse;

    }

    public static SubmitPrescriptionDetailsResponse Success(Long prescriptionId) {
        SubmitPrescriptionDetailsResponse submitPrescriptionDetailsResponse = new SubmitPrescriptionDetailsResponse();
        submitPrescriptionDetailsResponse.prescriptionId = prescriptionId;
        submitPrescriptionDetailsResponse.setResponseInfo(new ResponseInfo(true, "Prescription Added Successfully"));
        return submitPrescriptionDetailsResponse;

    }


}
