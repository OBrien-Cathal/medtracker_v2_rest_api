package com.cathalob.medtracker.payload.data;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SignInRecordData {
    private Long userModelId;
    private String username;
    private LocalDateTime signInTime;

}
