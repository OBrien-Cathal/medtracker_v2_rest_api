package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.config.SecurityConfig;
import com.cathalob.medtracker.mapper.RoleChangeMapper;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.payload.data.RoleChangeData;
import com.cathalob.medtracker.payload.request.rolechange.RoleChangeApprovalRequest;
import com.cathalob.medtracker.payload.request.rolechange.RoleChangeRequest;
import com.cathalob.medtracker.payload.response.generic.GenericResponse;
import com.cathalob.medtracker.payload.response.generic.ResponseInfo;
import com.cathalob.medtracker.payload.response.rolechange.RoleChangeStatusResponse;
import com.cathalob.medtracker.service.impl.AuthenticationServiceImpl;
import com.cathalob.medtracker.service.impl.CustomUserDetailsService;
import com.cathalob.medtracker.service.impl.JwtServiceImpl;
import com.cathalob.medtracker.service.impl.RoleChangeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.List;

import static com.cathalob.medtracker.testdata.UserModelBuilder.aUserModel;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = RoleChangeController.class)
class RoleChangeControllerTests {
    @MockBean
    private RoleChangeService roleChangeService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JwtServiceImpl jwtService;
    @MockBean
    private AuthenticationServiceImpl authenticationService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private RoleChangeMapper roleChangeMapper;


    @Value("${api.url}" + "/role-requests")
    private String controllerEndpoint;

    @DisplayName("Successful submit role change returns success response")
    @Test
    @WithMockUser("user@user.com")
    public void givenRoleRequest_when_then() throws Exception {
        //given - precondition or setup
        UserModel userModel = aUserModel().build();
        RoleChangeRequest roleChangeRequest = RoleChangeRequest.builder().newRole(USERROLE.PRACTITIONER).build();

        RoleChange roleChange = new RoleChange();

        given(roleChangeService.submitRoleChange(roleChangeRequest.getNewRole(), userModel.getUsername()))
                .willReturn(roleChange);
        given(roleChangeMapper.submitRoleRequestResponse(roleChange))
                .willReturn(GenericResponse.Success());

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(post(controllerEndpoint + "/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleChangeRequest)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", CoreMatchers.is(true)));
    }

    @DisplayName("Non existing role name causes unhandled exception")
    @Test
    @WithMockUser("user@user.com")
    public void givenBogusRoleName_when_then() throws Exception {
        //given - precondition or setup
        UserModel userModel = aUserModel().build();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("newRole", "foo");

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(post(controllerEndpoint + "/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toString()));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("Role change approval by ADMIN success returns success response")
    @Test
    @WithMockUser(username = "user@user.com", roles = {"ADMIN"})
    public void givenRoleChangeApprovalSucceeds_whenApproveRoleChange_thenReturnSuccessfulResponse() throws Exception {
        //given - precondition or setup
        UserModel userModel = aUserModel().build();
        RoleChangeApprovalRequest roleChangeApprovalRequest = new RoleChangeApprovalRequest(1L);

        RoleChange roleChange = new RoleChange();

        given(roleChangeService.approveRoleChange(1L, userModel.getUsername()))
                .willReturn(roleChange);
        given(roleChangeMapper.approveRoleChangeResponse(roleChange))
                .willReturn(GenericResponse.Success());

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(post(controllerEndpoint + "/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleChangeApprovalRequest)));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", CoreMatchers.is(true)));

    }

    @DisplayName("Get Role change status returns ok and status object")
    @Test
    @WithMockUser("user@user.com")
    public void givenRoleChangeOrNone_whenGetRoleChangeStatus_thenReturnRoleChangeStatusResponse() throws Exception {
        //given - precondition or setup
        UserModel userModel = aUserModel().build();
        HashMap<USERROLE, RoleChange> roleMap = new HashMap<>();

        given(roleChangeService.getRoleChangeStatus(userModel.getUsername()))
                .willReturn(roleMap);

        RoleChangeStatusResponse response = new RoleChangeStatusResponse();
        response.setResponseInfo(ResponseInfo.Success());

        given(roleChangeMapper.roleChangeStatusResponse(roleMap))
                .willReturn(response);

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(get(controllerEndpoint + "/status")
                .contentType(MediaType.APPLICATION_JSON));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseInfo.successful", CoreMatchers.is(true)));
    }

    @DisplayName("Get Unapproved role changes returns ok and list of role changes")
    @Test
    @WithMockUser("user@user.com")
    public void givenUnapprovedRoleChanges_whenGetUnapprovedRoleChanges_thenReturnRoleChangeDataList() throws Exception {
        //given - precondition or setup
        UserModel userModel = aUserModel().build();
        List<RoleChange> roleChangeList = List.of(new RoleChange(), new RoleChange());
        given(roleChangeService.getUnapprovedRoleChanges())
                .willReturn(roleChangeList);
        given(roleChangeMapper.roleChangeDataList(roleChangeList))
                .willReturn(List.of(new RoleChangeData(), new RoleChangeData()));

        // when - action or the behaviour that we are going test
        ResultActions usersResponse = mockMvc.perform(get(controllerEndpoint + "/unapproved")
                .contentType(MediaType.APPLICATION_JSON));

        // then - verify the output
        usersResponse
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }


}