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

    public static ResponseInfo Success(boolean successful, String message) {
        return new ResponseInfo(successful, message);
    }
    public static ResponseInfo Success() {
        return ResponseInfo.Success(true, "Success");
    }
}
