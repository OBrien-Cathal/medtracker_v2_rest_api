package com.cathalob.medtracker.payload.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Response {

    @Builder.Default
    private boolean successful = false;
    private String message;
    @Builder.Default
    private List<String> errors = new ArrayList<>();


    public Response(boolean b) {
        errors = new ArrayList<>();
        this.successful = b;
        this.message = b ? getSucceededMessage() : getFailedMessage();
    }

    public Response(boolean successful, String message) {
        errors = new ArrayList<>();
        this.successful = successful;
        this.message = message;
    }

    public Response(boolean successful, List<String> errors) {
        this.errors = errors;
        this.successful = successful;
        this.message = successful ? getSucceededMessage() : getFailedMessage();
    }

    public Response success() {
        this.successful = true;
        this.setMessage(getSucceededMessage());
        return this;
    }

    public Response success(String successMessage) {
        this.successful = true;
        this.setMessage(getSucceededMessage() + ": " + successMessage);
        return this;
    }

    public Response failure() {
        this.successful = false;
        this.setMessage(getFailedMessage());
        return this;
    }

    public Response failure(List<String> validationErrors) {
        this.successful = false;
        this.setMessage(getFailedMessage() + " Validation");
        this.setErrors(validationErrors);
        return this;
    }


    private static String getFailedMessage() {
        return "Failed";
    }

    private static String getSucceededMessage() {
        return "Succeeded";
    }
}
