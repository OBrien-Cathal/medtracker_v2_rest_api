package com.cathalob.medtracker.payload.response;

import com.cathalob.medtracker.payload.response.generic.Response;
import com.cathalob.medtracker.payload.response.generic.ResponseInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AddMedicationResponse extends Response {
    private Long medicationId;

    public AddMedicationResponse(ResponseInfo responseInfo) {
        super(responseInfo);
    }
    public AddMedicationResponse(Long medicationId, ResponseInfo responseInfo) {
        super(responseInfo);
        this.medicationId = medicationId;
    }

    public static AddMedicationResponse Success(Long medicationId) {
        return new AddMedicationResponse(medicationId,
                ResponseInfo.Success("Medication added successfully, Id: " + medicationId));
    }

    public static AddMedicationResponse Failed(List<String> errors) {
        return new AddMedicationResponse(ResponseInfo.Failed(errors));
    }


}