package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.mapper.RoleChangeMapper;
import com.cathalob.medtracker.payload.data.RoleChangeData;
import com.cathalob.medtracker.payload.request.rolechange.RoleChangeApprovalRequest;
import com.cathalob.medtracker.payload.request.rolechange.RoleChangeRequest;
import com.cathalob.medtracker.payload.response.generic.GenericResponse;
import com.cathalob.medtracker.payload.response.rolechange.RoleChangeStatusResponse;
import com.cathalob.medtracker.service.impl.RoleChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.url}" + "role-requests")
public class RoleChangeController {
    private final RoleChangeService roleChangeService;
    private final RoleChangeMapper roleChangeMapper;


    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/submit")
    public ResponseEntity<GenericResponse> submitRoleChangeRequest(
            @RequestBody RoleChangeRequest roleChangeRequest,
            Authentication authentication) {

        return ResponseEntity.ok(
                roleChangeMapper.submitRoleRequestResponse(
                        roleChangeService.submitRoleChange(roleChangeRequest.getNewRole(),
                                authentication.getName())));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/approve")
    public ResponseEntity<GenericResponse> approveRoleChange(
            @RequestBody RoleChangeApprovalRequest approvalRequest,
            Authentication authentication) {

        return ResponseEntity.ok(
                roleChangeMapper.approveRoleChangeResponse(
                        roleChangeService.approveRoleChange(approvalRequest.getRoleChangeRequestId(),
                                authentication.getName())));
    }

    @GetMapping("/status")
    public ResponseEntity<RoleChangeStatusResponse> getRoleChangeStatus(Authentication authentication) {

        return ResponseEntity.ok(
                roleChangeMapper.roleChangeStatusResponse(
                        roleChangeService.getRoleChangeStatus(authentication.getName())));
    }

    @GetMapping("/unapproved")
    public ResponseEntity<List<RoleChangeData>> getUnapprovedRoleChanges(Authentication authentication) {
        return ResponseEntity.ok(
                roleChangeMapper.roleChangeDataList(
                        roleChangeService.getUnapprovedRoleChanges()));
    }

}
