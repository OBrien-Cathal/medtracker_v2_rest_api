package com.cathalob.medtracker.service;

import com.cathalob.medtracker.exception.UserNotFound;
import com.cathalob.medtracker.model.PractitionerRoleRequest;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.payload.data.RoleChangeData;
import com.cathalob.medtracker.payload.response.Response;
import com.cathalob.medtracker.payload.response.RoleChangeStatusResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {


    UserModel findByLogin(String login) throws UserNotFound;


    List<UserModel> findByUserModelIds(List<Long> ids);

    Optional<UserModel> findUserModelById(Long id);

    List<UserModel> getUserModels();

    List<UserModel> getPractitionerUserModels();

    //  USER Role functions
    Response submitRoleChange(USERROLE newRole, String submitterUserName);

    Response approveRoleChange(Long roleChangeId, String approvedByUserName);

    RoleChangeStatusResponse getRoleChangeStatus(String username);

    List<RoleChangeData> getUnapprovedRoleChanges();



    boolean submitPasswordChangeRequest();
}
