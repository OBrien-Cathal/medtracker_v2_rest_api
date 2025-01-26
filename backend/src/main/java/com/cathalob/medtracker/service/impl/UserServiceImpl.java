package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.exception.UserNotFound;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.payload.data.RoleChangeData;
import com.cathalob.medtracker.payload.response.generic.GenericResponse;
import com.cathalob.medtracker.payload.response.rolechange.RoleChangeStatusResponse;
import com.cathalob.medtracker.repository.RoleChangeRepository;
import com.cathalob.medtracker.repository.UserModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
@EnableMethodSecurity
public class UserServiceImpl implements com.cathalob.medtracker.service.UserService {
    private final UserModelRepository userModelRepository;
    private final RoleChangeRepository roleChangeRepository;

    @Override
    public UserModel findByLogin(String username) throws UserNotFound {
        Optional<UserModel> maybeUserModel = userModelRepository.findByUsername(username);
        if (maybeUserModel.isEmpty()) throw new UserNotFound(username);
        return maybeUserModel.orElse(null);
    }

    @Override
    public List<UserModel> findByUserModelIds(List<Long> ids) {
        return userModelRepository.findAll();
    }
    @Override
    public Optional<UserModel> findUserModelById(Long id) {
        return userModelRepository.findById(id);
    }
    @Override
    public List<UserModel> getUserModels() {
        return findByUserModelIds(List.of());
    }


    @Override
    public List<UserModel> getPractitionerUserModels() {
        return userModelRepository.findByRole(USERROLE.PRACTITIONER);
    }

    public Map<Long, UserModel> getUserModelsById() {
        return getUserModels()
                .stream().collect(Collectors.toMap(UserModel::getId, Function.identity()));
    }

    //    NEW ROLE functions
    @Override
    public GenericResponse submitRoleChange(USERROLE newRole, String submitterUserName) {
        UserModel subbmiterUserModel = findByLogin(submitterUserName);
        RoleChange roleChange = new RoleChange();
        roleChange.setNewRole(newRole);
        roleChange.setUserModel(subbmiterUserModel);
        roleChange.setOldRole(subbmiterUserModel.getRole());
        roleChange.setRequestTime(LocalDateTime.now());

        List<String> errors = validateRoleChangeSubmission(roleChange);
        if (!errors.isEmpty()) {
            return GenericResponse.Failed(errors);
        }

        RoleChange savedRoleChange = roleChangeRepository.save(roleChange);
        return GenericResponse.Success("Request pending with ID: " + savedRoleChange.getId());
    }

    private List<String> validateRoleChangeSubmission(RoleChange roleChange) {
        ArrayList<String> errors = new ArrayList<>();
        List<RoleChange> unapproved = roleChangeRepository.findByUserModelIdAndNewRoleAndApprovedById(
                roleChange.getUserModel().getId(),
                roleChange.getNewRole(),
                null);
        if (roleChange.getUserModel().getRole() != USERROLE.USER) {
            errors.add(String.format("Current User Role: %s is not a candidate for role change to: %s",
                    roleChange.getUserModel().getRole().name(),
                    roleChange.getNewRole()));
        }
        if (!unapproved.isEmpty())
            errors.add("Unapproved request already submitted for role: " + roleChange.getNewRole().name());
        return errors;
    }

    @Override
    public GenericResponse approveRoleChange(Long roleChangeId, String approvedByUserName) {
        UserModel approvedBy = findByLogin(approvedByUserName);
//    replace line below when adding method security

        if (approvedBy == null || approvedBy.getRole() != USERROLE.ADMIN) {
            return GenericResponse.Failed("insufficient privileges");
        }

        Optional<RoleChange> maybeRoleChange = roleChangeRepository.findById(roleChangeId);
        if (maybeRoleChange.isEmpty())return GenericResponse.Failed("Role change not found");

        RoleChange roleChange = maybeRoleChange.get();

        List<String> errors = validateRoleChangeApproval(roleChange);
        if (!errors.isEmpty()) {
            return GenericResponse.Failed("Validation Failed", errors);
        }

        roleChange.setApprovedBy(approvedBy);
        roleChange.setApprovalTime(LocalDateTime.now());
        UserModel roleChangeUserModel = roleChange.getUserModel();
        roleChangeUserModel.setRole(roleChange.getNewRole());

        roleChangeRepository.save(roleChange);
        userModelRepository.save(roleChangeUserModel);
        return GenericResponse.Success();
    }

    private List<String> validateRoleChangeApproval(RoleChange roleChange) {
        ArrayList<String> errors = new ArrayList<>();
        if (roleChange.getApprovedBy() != null)
            errors.add(String.format(
                    "Role change with Id: %s was already approved by: %s at: %s",
                    roleChange.getId(),
                    roleChange.getApprovedBy().getUsername(),
                    roleChange.getApprovalTime().toString()));
        if (roleChange.getUserModel().getRole() != USERROLE.USER) {
            errors.add(String.format("User Role: %s is not a candidate for role change to: %s",
                    roleChange.getUserModel().getRole().name(),
                    roleChange.getNewRole()));
        }

        return errors;
    }

    @Override
    public RoleChangeStatusResponse getRoleChangeStatus(String username) {
        List<RoleChange> byUserModelId = roleChangeRepository.findByUserModelId(findByLogin(username).getId());
        Map<USERROLE, RoleChange> userroleRoleChangeMap =
                byUserModelId.stream().collect(Collectors.toMap(RoleChange::getNewRole, Function.identity()));

        RoleChange practitionerRoleChange = userroleRoleChangeMap.get(USERROLE.PRACTITIONER);
        RoleChange adminRoleChange = userroleRoleChangeMap.get(USERROLE.ADMIN);

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
        return roleChangeStatusResponse;
    }

    private static String getRoleChangeStatusString(RoleChange aRoleChange) {
        return aRoleChange.getApprovedBy() == null ? "Pending" : "Approved";
    }

    @Override
    public List<RoleChangeData> getUnapprovedRoleChanges() {
        return roleChangeRepository.findByApprovedById(null).stream().filter(roleChange ->
                roleChange.getUserModel().getRole().equals(USERROLE.USER)
        ).map(roleChange -> {
            RoleChangeData roleChangeData = new RoleChangeData();
            roleChangeData.setId(roleChange.getId());
            roleChangeData.setUserRole(roleChange.getNewRole());
            roleChangeData.setStatus(getRoleChangeStatusString(roleChange));
            roleChangeData.setUserModelId(roleChange.getUserModel().getId());
            return roleChangeData;
        }).toList();

    }


    @Override
    public boolean submitPasswordChangeRequest() {
        return false;
    }
}
