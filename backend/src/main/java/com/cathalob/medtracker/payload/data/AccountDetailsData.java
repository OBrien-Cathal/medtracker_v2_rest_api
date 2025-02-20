package com.cathalob.medtracker.payload.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class AccountDetailsData {

    private String firstName;
    private String surname;
    private Long userModelId;
    private String email;

}
