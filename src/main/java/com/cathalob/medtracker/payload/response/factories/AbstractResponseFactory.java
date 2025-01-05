package com.cathalob.medtracker.payload.response.factories;

import com.cathalob.medtracker.payload.response.AbstractResponse;

import java.util.ArrayList;
import java.util.List;

public class AbstractResponseFactory {
    protected static void fillFailedDefaults(AbstractResponse response, List<String> errors){
        response.setSuccessful(false);
        response.setMessage("Failed");
        response.setErrors(errors);

    }
     protected static void fillSuccessfulDefaults(AbstractResponse response){
        response.setSuccessful(true);
        response.setMessage("Success");
        response.setErrors(new ArrayList<>());
    }

}
