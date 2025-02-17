package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.factory.RoleChangeServiceFactory;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.repository.RoleChangeRepository;
import com.cathalob.medtracker.validate.service.RoleChangeServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static com.cathalob.medtracker.testdata.RoleChangeBuilder.aRoleChange;
import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class RoleChangeServiceTests {
    private UserModel userModel;

    @Mock
    private UserServiceImpl userService;
    @Mock
    private RoleChangeRepository roleChangeRepository;
    @Mock
    private RoleChangeServiceFactory roleChangeServiceFactory;
    @Mock
    private RoleChangeServiceValidator serviceValidator;

    @InjectMocks
    private RoleChangeService roleChangeService;


    @BeforeEach
    public void setUp() {
        this.userModel = aUserModel().withId(1L).build();
    }


    @DisplayName("Successful submit role change returns saved RoleChange")
    @Test
    public void givenSuccessfulSubmitRoleChange_whenSubmitRoleChange_thenReturnSavedRoleChange() {
        //given - precondition or setup
        RoleChange roleChange = aRoleChange().withId(1L).build();
        USERROLE newRole = USERROLE.PRACTITIONER;


        given(userService.findByLogin(roleChange.getUserModel().getUsername()))
                .willReturn(userModel);
        given(roleChangeServiceFactory.roleChange(userModel, newRole))
                .willReturn(roleChange);

        List<RoleChange> notApproved = List.of();
        given(roleChangeRepository.findByUserModelIdAndNewRoleAndApprovedById(
                roleChange.getUserModel().getId(),
                roleChange.getNewRole(),
                null))
                .willReturn(notApproved);
//        given(serviceValidator.validateSubmitRoleChange(roleChange, notApproved))
        given(roleChangeRepository.save(roleChange))
                .willReturn(roleChange);

        // when - action or the behaviour that we are going test
        RoleChange response = roleChangeService.submitRoleChange(newRole,
                userModel.getUsername());
        // then - verify the output

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
    }


    @DisplayName("Successful get unapproved role changes returns list of saved RoleChange")
    @Test
    public void givenRoleChanges_whenGetUnapprovedRoleChanges_thenReturnRoleChanges() {
        RoleChange roleChange = aRoleChange().withId(1L).build();
        roleChange.setUserModel(userModel);
        RoleChange roleChange2 = aRoleChange().withNewRole(USERROLE.ADMIN).withId(2L).build();
        roleChange2.getUserModel().setId(2L);


        given(roleChangeRepository.findByApprovedById(null))
                .willReturn(List.of(roleChange, roleChange2));


        // when - action or the behaviour that we are going test
        List<RoleChange> unapprovedRoleChanges = roleChangeService.getUnapprovedRoleChanges();
        // then - verify the output
        assertThat(unapprovedRoleChanges.size()).isEqualTo(2);
        assertThat(unapprovedRoleChanges.stream().anyMatch(roleChangeData -> roleChangeData.getNewRole().equals(USERROLE.PRACTITIONER)));
        assertThat(unapprovedRoleChanges.stream().anyMatch(roleChangeData -> roleChangeData.getNewRole().equals(USERROLE.ADMIN)));

        verify(roleChangeRepository, times(1)).findByApprovedById(null);
    }


    @Test
    public void givenExistingRoleChange_whenApproveRoleChange_thenReturnSuccessfulResponse() {
        //given - precondition or setup
        RoleChange roleChange = aRoleChange().withId(1L).build();
        roleChange.setUserModel(userModel);
        UserModel approvedBy = aUserModel().withRole(USERROLE.ADMIN).withUsername("admin").build();
        given(userService.findByLogin(approvedBy.getUsername())).willReturn(approvedBy);
        given(roleChangeRepository.findById(roleChange.getId()))
                .willReturn(Optional.of(roleChange));

        given(roleChangeRepository.save(roleChange)).willReturn(roleChange);

        // when - action or the behaviour that we are going test
        RoleChange response = roleChangeService.approveRoleChange(roleChange.getId(), approvedBy.getUsername());
        // then - verify the output
        assertThat(response).isNotNull();
        verify(roleChangeRepository, times(1)).save(any(RoleChange.class));
    }




    @Disabled("Validation error returned if unapproved role change exists for the same type of role and user")
    @Test
    public void givenExistingUnapprovedRoleChangeForRoleAndUser_when_then() {
        //given - precondition or setup

        // when - action or the behaviour that we are going test

        // then - verify the output
    }

    @Disabled("Validation error returned if current user role can not be changed")
    @Test
    public void givenCurrentAdminRole_when_then() {
        //given - precondition or setup

        // when - action or the behaviour that we are going test

        // then - verify the output
    }
}