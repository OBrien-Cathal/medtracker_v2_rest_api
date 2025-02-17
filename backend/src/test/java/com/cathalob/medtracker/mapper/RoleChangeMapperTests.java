package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.payload.response.rolechange.RoleChangeStatusResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;

import static com.cathalob.medtracker.testdata.RoleChangeBuilder.aRoleChange;
import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;


class RoleChangeMapperTests {


    @Test
    public void givenExistingApprovedPractitionerRoleChange_whenGetRoleChangeStatus_thenReturnRoleChangeStatus() {
        RoleChangeStatusResponse roleChangeStatus = roleChangeStatusMatches(
                USERROLE.PRACTITIONER,
                true);
        // then - verify the output
        System.out.println(roleChangeStatus);
        assertThat(roleChangeStatus.getAdminRoleChange().getId()).isEqualTo(null);
        assertThat(roleChangeStatus.getAdminRoleChange().getStatus()).isEqualTo("Unrequested");
        assertThat(roleChangeStatus.getPractitionerRoleChange().getId()).isEqualTo(1L);
        assertThat(roleChangeStatus.getPractitionerRoleChange().getStatus()).isEqualTo("Approved");

    }

    @Test
    public void givenExistingUnapprovedPractitionerRoleChange_whenGetRoleChangeStatus_thenReturnRoleChangeStatus() {
        RoleChangeStatusResponse roleChangeStatus = roleChangeStatusMatches(
                USERROLE.PRACTITIONER,
                false);
        // then - verify the output
        System.out.println(roleChangeStatus);
        assertThat(roleChangeStatus.getAdminRoleChange().getId()).isEqualTo(null);
        assertThat(roleChangeStatus.getAdminRoleChange().getStatus()).isEqualTo("Unrequested");
        assertThat(roleChangeStatus.getPractitionerRoleChange().getId()).isEqualTo(1L);
        assertThat(roleChangeStatus.getPractitionerRoleChange().getStatus()).isEqualTo("Pending");

    }

    @Test
    public void givenExistingApprovedAdminRoleChange_whenGetRoleChangeStatus_thenReturnRoleChangeStatus() {
        RoleChangeStatusResponse roleChangeStatus = roleChangeStatusMatches(
                USERROLE.ADMIN,
                true);
        // then - verify the output
        System.out.println(roleChangeStatus);
        assertThat(roleChangeStatus.getAdminRoleChange().getId()).isEqualTo(1L);
        assertThat(roleChangeStatus.getAdminRoleChange().getStatus()).isEqualTo("Approved");
        assertThat(roleChangeStatus.getPractitionerRoleChange().getId()).isEqualTo(null);
        assertThat(roleChangeStatus.getPractitionerRoleChange().getStatus()).isEqualTo("Unrequested");

    }

    @Test
    public void givenExistingUnapprovedAdminRoleChange_whenGetRoleChangeStatus_thenReturnRoleChangeStatus() {
        RoleChangeStatusResponse roleChangeStatus = roleChangeStatusMatches(
                USERROLE.ADMIN,
                false);

        // then - verify the output
        System.out.println(roleChangeStatus);
        assertThat(roleChangeStatus.getAdminRoleChange().getId()).isEqualTo(1L);
        assertThat(roleChangeStatus.getAdminRoleChange().getStatus()).isEqualTo("Pending");
        assertThat(roleChangeStatus.getPractitionerRoleChange().getId()).isEqualTo(null);
        assertThat(roleChangeStatus.getPractitionerRoleChange().getStatus()).isEqualTo("Unrequested");

    }

    private RoleChangeStatusResponse roleChangeStatusMatches(USERROLE newRole, boolean approved) {
        //given - precondition or setup
        RoleChange roleChange = aRoleChange().withId(1L).build();
        roleChange.setNewRole(newRole);
        roleChange.setUserModel(aUserModel().build());

        System.out.println(roleChange);
        if (approved) {
            UserModel approvedBy = aUserModel().withRole(USERROLE.ADMIN).withUsername("admin").build();
            roleChange.setApprovedBy(approvedBy);
            roleChange.setApprovalTime(LocalDateTime.now());
        }
        HashMap<USERROLE, RoleChange> roleMap = new HashMap<>();
        roleMap.put(roleChange.getNewRole(), roleChange);

        return new RoleChangeMapper().roleChangeStatusResponse(roleMap);


    }

}