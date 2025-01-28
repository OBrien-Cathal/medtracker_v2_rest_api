package com.cathalob.medtracker.payload.response;

import com.cathalob.medtracker.payload.data.BloodPressureData;
import com.cathalob.medtracker.payload.response.generic.Response;
import com.cathalob.medtracker.payload.response.generic.ResponseInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class BloodPressureDataRequestResponse extends Response {
    private List<BloodPressureData> readings;

    public BloodPressureDataRequestResponse(ResponseInfo responseInfo, List<BloodPressureData> readings) {
        super(responseInfo);
        this.readings = readings;
    }

    public BloodPressureDataRequestResponse(ResponseInfo responseInfo) {
        super(responseInfo);
    }

    public static BloodPressureDataRequestResponse Failed() {
        return new BloodPressureDataRequestResponse(ResponseInfo.Failed());
    }

    public static BloodPressureDataRequestResponse Failed(List<String> errors) {
        return new BloodPressureDataRequestResponse(ResponseInfo.Failed(errors));
    }


    public static BloodPressureDataRequestResponse Success() {
        return new BloodPressureDataRequestResponse(ResponseInfo.Success());
    }

    public static BloodPressureDataRequestResponse Success(List<BloodPressureData> readings) {
        return new BloodPressureDataRequestResponse(ResponseInfo.Success(), readings);
    }
}
