package com.cathalob.medtracker.service;

import com.cathalob.medtracker.exception.UserNotFound;
import com.cathalob.medtracker.model.PractitionerRoleRequest;
import com.cathalob.medtracker.model.UserModel;

import java.util.List;
import java.util.Optional;

public interface UserService {


    UserModel findByLogin(String login) throws UserNotFound;

    List<UserModel> getUserModels();

    List<UserModel> getPatientUserModels();

    //  USER Role functions
    boolean submitPractitionerRoleRequest(String username);

    PractitionerRoleRequest savePractitionerRoleRequest(PractitionerRoleRequest practitionerRoleRequest, UserModel userModel);

    Optional<PractitionerRoleRequest> getPractitionerRoleRequest(String username);
    public Optional<PractitionerRoleRequest> getPractitionerRoleRequest(Long userModelId);

    //ADMIN user functions
    List<PractitionerRoleRequest> getPractitionerRoleRequests();

    boolean approvePractitionerRoleRequests(List<PractitionerRoleRequest> requests) ;

    boolean submitPasswordChangeRequest();
}
