package com.cathalob.medtracker.payload.response.generic;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder

public class ResponseInfo {
    private boolean successful;
    private String message;
    private List<String> errors;

    public ResponseInfo(boolean successful, String message, List<String> errors) {
        this.successful = successful;
        this.message = message;
        this.errors = errors;
    }

    public ResponseInfo(List<String> errors) {
        this.successful = false;
        this.message = "Failed";
        this.errors = errors;
    }

    public ResponseInfo(boolean successful, String message) {
        this.successful = successful;
        this.message = message;
        this.errors = new ArrayList<>();
    }

    public ResponseInfo() {
    }

    public static ResponseInfo Success(String message) {
        return new ResponseInfo(true, message);
    }

    public static ResponseInfo Success() {
        return ResponseInfo.Success("Success");
    }

    public static ResponseInfo Failed() {
        return new ResponseInfo(false, "Failed");
    }

    public static ResponseInfo Failed(String message) {
        return new ResponseInfo(false, message);
    }

    public static ResponseInfo Failed(List<String> errors) {
        return new ResponseInfo(errors);
    }

    public static ResponseInfo Failed(String message, List<String> errors) {
        return new ResponseInfo(false, message, errors);
    }
}
