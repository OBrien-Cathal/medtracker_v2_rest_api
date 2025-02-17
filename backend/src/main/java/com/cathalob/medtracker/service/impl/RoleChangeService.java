package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.factory.RoleChangeServiceFactory;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.repository.RoleChangeRepository;
import com.cathalob.medtracker.validate.service.RoleChangeServiceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class RoleChangeService {
    private final UserServiceImpl userService;
    private final RoleChangeRepository roleChangeRepository;
    private final RoleChangeServiceFactory roleChangeServiceFactory;
    private final RoleChangeServiceValidator serviceValidator;

    public RoleChange submitRoleChange(USERROLE newRole, String submitterUserName) {
        UserModel subbmiterUserModel = userService.findByLogin(submitterUserName);
        RoleChange roleChange = roleChangeServiceFactory.roleChange(subbmiterUserModel, newRole);


        List<RoleChange> unapproved = roleChangeRepository.findByUserModelIdAndNewRoleAndApprovedById(
                roleChange.getUserModel().getId(),
                roleChange.getNewRole(),
                null);

        serviceValidator.validateSubmitRoleChange(roleChange, unapproved);

        return roleChangeRepository.save(roleChange);
    }


    public RoleChange approveRoleChange(Long roleChangeId, String approvedByUserName) {
        UserModel approvedBy = userService.findByLogin(approvedByUserName);

        RoleChange roleChange = roleChangeRepository.findById(roleChangeId).orElse(null);

        serviceValidator.validateApproveRoleChange(roleChange, approvedBy);

        if (roleChange == null) return null;

        roleChange.setApprovedBy(approvedBy);
        roleChange.setApprovalTime(LocalDateTime.now());
        UserModel roleChangeUserModel = roleChange.getUserModel();
        roleChangeUserModel.setRole(roleChange.getNewRole());

        RoleChange savedRoleChange = roleChangeRepository.save(roleChange);
        userService.saveUserModel(roleChangeUserModel);

        return savedRoleChange;
    }

    public Map<USERROLE, RoleChange> getRoleChangeStatus(String username) {

        return roleChangeRepository.findByUserModelId(
                userService.findByLogin(username).getId())
                .stream()
                .collect(Collectors.toMap(RoleChange::getNewRole, Function.identity()));
    }

    public List<RoleChange> getUnapprovedRoleChanges() {
        return roleChangeRepository.findByApprovedById(null).stream().filter(roleChange ->
                roleChange.getUserModel().getRole().equals(USERROLE.USER)).toList();
    }
}
