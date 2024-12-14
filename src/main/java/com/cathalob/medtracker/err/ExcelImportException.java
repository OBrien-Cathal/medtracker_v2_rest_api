package com.cathalob.medtracker.err;

public class ExcelImportException extends RuntimeException {
    public ExcelImportException(String message) {
        super(message);
    }
}
