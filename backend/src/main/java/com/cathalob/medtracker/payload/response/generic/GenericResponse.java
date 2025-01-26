package com.cathalob.medtracker.payload.response.generic;

import java.util.List;

public class GenericResponse extends Response {
    public GenericResponse(ResponseInfo responseInfo) {
        super(responseInfo);
    }

    public static GenericResponse Success() {
        return new GenericResponse(ResponseInfo.Success());
    }

    public static GenericResponse Success(String message) {
        return new GenericResponse(ResponseInfo.Success(message));
    }

    public static GenericResponse Failed() {
        return new GenericResponse(ResponseInfo.Failed());
    }

    public static GenericResponse Failed(List<String> errors) {
        return new GenericResponse(ResponseInfo.Failed(errors));
    }

    public static GenericResponse Failed(String message) {
        return new GenericResponse(ResponseInfo.Failed(message));
    }

    public static GenericResponse Failed(String message, List<String> errors) {
        return new GenericResponse(ResponseInfo.Failed(message, errors));
    }
}
