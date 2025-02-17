package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.payload.data.RoleChangeData;
import com.cathalob.medtracker.payload.response.generic.GenericResponse;
import com.cathalob.medtracker.payload.response.generic.ResponseInfo;
import com.cathalob.medtracker.payload.response.rolechange.RoleChangeStatusResponse;

import java.util.List;
import java.util.Map;

public class RoleChangeMapper {
    public String roleChangeSubmitString(RoleChange roleChange) {
        return "Request pending with ID: " + roleChange.getId();
    }


    public GenericResponse submitRoleRequestResponse(RoleChange roleChange) {
        return GenericResponse.Success(roleChangeSubmitString(roleChange));
    }

    public GenericResponse approveRoleChangeResponse(RoleChange roleChange) {
        return GenericResponse.Success(roleChangeApprovalString(roleChange));
    }

    public String roleChangeApprovalString(RoleChange roleChange) {
        return "Request for role change with ID: " + roleChange.getId() + " approved";
    }

    public List<RoleChangeData> roleChangeDataList(List<RoleChange> roleChangeList) {
        return RoleChangeDataList(roleChangeList);
    }

    public static List<RoleChangeData> RoleChangeDataList(List<RoleChange> roleChangeList) {

        return roleChangeList.stream().map(roleChange -> {
            RoleChangeData roleChangeData = new RoleChangeData();
            roleChangeData.setId(roleChange.getId());
            roleChangeData.setUserRole(roleChange.getNewRole());
            roleChangeData.setStatus(getRoleChangeStatusString(roleChange));
            roleChangeData.setUserModelId(roleChange.getUserModel().getId());
            return roleChangeData;
        }).toList();

    }

    private static String getRoleChangeStatusString(RoleChange aRoleChange) {
        return aRoleChange.getApprovedBy() == null ? "Pending" : "Approved";
    }


    public RoleChangeStatusResponse roleChangeStatusResponse(Map<USERROLE, RoleChange> roleChangeMap) {
        return RoleChangeStatusResponse(roleChangeMap);
    }

    public RoleChangeStatusResponse RoleChangeStatusResponse(Map<USERROLE, RoleChange> roleChangeMap) {

        RoleChange practitionerRoleChange = roleChangeMap.get(USERROLE.PRACTITIONER);
        RoleChange adminRoleChange = roleChangeMap.get(USERROLE.ADMIN);

        RoleChangeStatusResponse roleChangeStatusResponse = new RoleChangeStatusResponse();

        RoleChangeData adminRoleData = new RoleChangeData();
        adminRoleData.setUserRole(USERROLE.ADMIN);

        RoleChangeData practitionerRoleChangeData = new RoleChangeData();
        practitionerRoleChangeData.setUserRole(USERROLE.PRACTITIONER);
        if (adminRoleChange == null) {
            adminRoleData.setStatus("Unrequested");
        } else {
            adminRoleData.setUserModelId(adminRoleChange.getUserModel().getId());
            adminRoleData.setId(adminRoleChange.getId());
            adminRoleData.setStatus(getRoleChangeStatusString(adminRoleChange));
        }
        if (practitionerRoleChange == null) {
            practitionerRoleChangeData.setStatus("Unrequested");
        } else {
            practitionerRoleChangeData.setUserModelId(practitionerRoleChange.getUserModel().getId());
            practitionerRoleChangeData.setId(practitionerRoleChange.getId());
            practitionerRoleChangeData.setStatus(getRoleChangeStatusString(practitionerRoleChange));
        }

        roleChangeStatusResponse.setPractitionerRoleChange(practitionerRoleChangeData);
        roleChangeStatusResponse.setAdminRoleChange(adminRoleData);
        roleChangeStatusResponse.setResponseInfo(ResponseInfo.Success());

        return roleChangeStatusResponse;
    }
}
