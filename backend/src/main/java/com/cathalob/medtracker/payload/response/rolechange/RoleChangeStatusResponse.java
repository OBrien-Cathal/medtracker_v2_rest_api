package com.cathalob.medtracker.payload.response.rolechange;

import com.cathalob.medtracker.payload.data.RoleChangeData;
import com.cathalob.medtracker.payload.response.generic.Response;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)

public class RoleChangeStatusResponse extends Response {
    private RoleChangeData practitionerRoleChange;
    private RoleChangeData adminRoleChange;

}
