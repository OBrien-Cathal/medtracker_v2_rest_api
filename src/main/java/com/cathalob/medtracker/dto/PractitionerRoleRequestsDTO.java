package com.cathalob.medtracker.dto;

import com.cathalob.medtracker.model.PractitionerRoleRequest;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Data

public class PractitionerRoleRequestsDTO {
    public List <PractitionerRoleRequest> requests;

    public PractitionerRoleRequestsDTO() {
        requests = new ArrayList<>();

    }
}
