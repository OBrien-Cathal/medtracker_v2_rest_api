package com.cathalob.medtracker.payload.response;

import com.cathalob.medtracker.payload.data.PrescriptionDetailsData;
import com.cathalob.medtracker.payload.response.generic.Response2;

import com.cathalob.medtracker.payload.response.generic.ResponseInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
public class GetPrescriptionDetailsResponse extends Response2 {
    private PrescriptionDetailsData prescriptionDetails;

    public GetPrescriptionDetailsResponse() {
    }

    public static GetPrescriptionDetailsResponse Failed(List<String> errors){
        GetPrescriptionDetailsResponse getPrescriptionDetailsResponse = new GetPrescriptionDetailsResponse();
        getPrescriptionDetailsResponse.responseInfo.setErrors(errors);
        return getPrescriptionDetailsResponse;
    }

    public static GetPrescriptionDetailsResponse Success(PrescriptionDetailsData prescriptionDetailsData){
        GetPrescriptionDetailsResponse getPrescriptionDetailsResponse = new GetPrescriptionDetailsResponse();
        getPrescriptionDetailsResponse.prescriptionDetails = prescriptionDetailsData;
        getPrescriptionDetailsResponse.setResponseInfo(ResponseInfo.Success());
        return getPrescriptionDetailsResponse;

    }

}
