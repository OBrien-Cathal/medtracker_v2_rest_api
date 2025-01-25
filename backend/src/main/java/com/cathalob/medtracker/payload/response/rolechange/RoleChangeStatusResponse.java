package com.cathalob.medtracker.payload.response.rolechange;

import com.cathalob.medtracker.payload.data.RoleChangeData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoleChangeStatusResponse {
    private RoleChangeData practitionerRoleChange;
    private RoleChangeData adminRoleChange;


}
