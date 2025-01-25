package com.cathalob.medtracker.payload.request.rolechange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleChangeApprovalRequest {
    private Long roleChangeRequestId;

}
