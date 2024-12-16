package com.cathalob.medtracker.exception;

public class InternalException extends MedTrackerException{

    public InternalException(String message) {
        super(message);
    }

    public Integer errorCode(){
         return -1;
     };
}
