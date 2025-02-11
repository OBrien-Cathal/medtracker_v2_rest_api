package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.payload.data.RoleChangeData;
import com.cathalob.medtracker.payload.request.rolechange.RoleChangeApprovalRequest;
import com.cathalob.medtracker.payload.request.rolechange.RoleChangeRequest;
import com.cathalob.medtracker.payload.response.generic.GenericResponse;
import com.cathalob.medtracker.payload.response.rolechange.RoleChangeStatusResponse;
import com.cathalob.medtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserModel>> getUserModels() {
        return ResponseEntity.ok(userService.getUserModels());
    }

    @PostMapping("/role-requests/submit")
    public ResponseEntity<GenericResponse> submitRoleChangeRequest(
            @RequestBody RoleChangeRequest roleChangeRequest,
            Authentication authentication) {
        GenericResponse requestResponse = userService.submitRoleChange(roleChangeRequest.getNewRole(),
                authentication.getName());
        return ResponseEntity.ok(requestResponse);
    }

    @PostMapping("/role-requests/approve")
    public ResponseEntity<GenericResponse> approveRoleChange(
            @RequestBody RoleChangeApprovalRequest approvalRequest,
            Authentication authentication) {
        GenericResponse requestResponse = userService.approveRoleChange(approvalRequest.getRoleChangeRequestId(),
                authentication.getName());
        return ResponseEntity.ok(requestResponse);
    }
    @GetMapping("/role-requests/status")
    public ResponseEntity<RoleChangeStatusResponse> getRoleChangeStatus(Authentication authentication) {
        RoleChangeStatusResponse response = userService.getRoleChangeStatus(authentication.getName());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/role-requests/unapproved")
    public ResponseEntity<List<RoleChangeData>> getUnapprovedRoleChanges(Authentication authentication) {
        List<RoleChangeData> response = userService.getUnapprovedRoleChanges();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/practitioners")
    public ResponseEntity<List<UserModel>> getPractitionerUserModels() {
        return ResponseEntity.ok(userService.getPractitionerUserModels());
    }

}
