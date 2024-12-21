package com.cathalob.medtracker.config;

import com.cathalob.medtracker.exception.ExternalException;
import com.cathalob.medtracker.exception.InternalException;
import com.cathalob.medtracker.exception.MedTrackerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Map<String, List<String>>> handleGeneralExceptions(Exception ex) {
        return new ResponseEntity<>(genericObfuscatingErrorsMap(ex), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).toList();
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Map<String, List<String>>> handleRuntimeExceptions(RuntimeException ex) {
        return new ResponseEntity<>(genericObfuscatingErrorsMap(ex), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InternalException.class)
    public final ResponseEntity<Map<String, List<String>>> handleMedTrackerExceptions(InternalException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(getErrorsMap(ExternalException.getErrorMessageWithCode(ex)),
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ExternalException.class)
    public final ResponseEntity<Map<String, List<String>>> handleMedTrackerExceptions(ExternalException ex) {
        return new ResponseEntity<>(getErrorsMap(ex.getMessage()),
                new HttpHeaders(),
                HttpStatus.OK);
    }

    private Map<String, List<String>> getErrorsMap(MedTrackerException e) {
        List<String> errors = Collections.singletonList(e.getMessage());
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }
    private Map<String, List<String>> getErrorsMap(String errorString) {
        List<String> errors = Collections.singletonList(errorString);
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }
    private String genericObfuscatingError(){
        return "The server reported an error";
    }
    private Map<String, List<String>> genericObfuscatingErrorsMap(Exception exception){
        log.error(exception.getMessage());
        List<String> errors = List.of(genericObfuscatingError());
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }
}
