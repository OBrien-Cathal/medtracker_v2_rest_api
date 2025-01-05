package com.cathalob.medtracker.service.api.impl;

import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.factories.PatientRegistrationFactory;
import com.cathalob.medtracker.model.userroles.RoleChange;
import com.cathalob.medtracker.payload.data.PatientRegistrationData;
import com.cathalob.medtracker.payload.data.factories.PatientRegistrationDataFactory;
import com.cathalob.medtracker.payload.response.ApprovePatientRegistrationResponse;
import com.cathalob.medtracker.payload.response.PatientRegistrationResponse;
import com.cathalob.medtracker.payload.response.factories.ApprovePatientRegistrationResponseFactory;
import com.cathalob.medtracker.payload.response.factories.PatientRegistrationResponseFactory;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.repository.RoleChangeRepository;
import com.cathalob.medtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class PatientsServiceApi {
    private final UserService userService;
    private final RoleChangeRepository roleChangeRepository;
    private final PatientRegistrationRepository patientRegistrationRepository;

    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public List<UserModel> getPatientUserModels(String username) {
        UserModel userModel = userService.findByLogin(username);
        if (userModel == null || !userModel.getRole().equals(USERROLE.PRACTITIONER)) return List.of();
        List<Long> patientUserModelIds = patientRegistrationRepository.findByPractitionerUserModel(userModel)
                .stream()
                .map((patientRegistration -> patientRegistration.getUserModel().getId())).toList();
        return userService.findUserModelsById(patientUserModelIds);
    }


    public PatientRegistrationResponse registerPatient(String username, Long practitionerId) {
        UserModel toRegister = userService.findByLogin(username);
        Optional<UserModel> maybePractitioner = userService.findUserModelById(practitionerId);

        if (maybePractitioner.isEmpty()) {
            return PatientRegistrationResponseFactory.Failed(List.of("Practitioner does not exist"));
        }

        if (!toRegister.getRole().equals(USERROLE.USER)) {
            return PatientRegistrationResponseFactory.Failed(List.of("User does not have USER role"));
        }
        PatientRegistration patientRegistration = PatientRegistrationFactory.PatientRegistration(toRegister,maybePractitioner.get(),false);
        PatientRegistration saved = patientRegistrationRepository.save(patientRegistration);

        return PatientRegistrationResponseFactory.Successful(saved);
    }


    public ApprovePatientRegistrationResponse approvePatientRegistration(String username, Long patientRegistrationId) {

        Optional<PatientRegistration> reg = patientRegistrationRepository.findById(patientRegistrationId);

        ArrayList<String> errors = new ArrayList<>();
        if (reg.isEmpty()) {
            errors.add("Registration with id " + patientRegistrationId + " does not exist");
            return ApprovePatientRegistrationResponseFactory.Failed(errors);
        }
        PatientRegistration patientRegistration = reg.get();
        if (patientRegistration.isRegistered())
            errors.add("Registration is already approved for reg id: " + patientRegistrationId);

        UserModel toRegister = patientRegistration.getUserModel();
        if (toRegister.getRole().equals(USERROLE.USER)) {
//            Add role change if this is the first time a user has requested a patient registration
            RoleChange roleChange = new RoleChange();
            roleChange.setNewRole(USERROLE.PATIENT);
            roleChange.setUserModel(toRegister);
            roleChange.setOldRole(toRegister.getRole());
            roleChange.setRequestTime(LocalDateTime.now());
            roleChange.setApprovedBy(patientRegistration.getPractitionerUserModel());
            roleChange.setApprovalTime(LocalDateTime.now());
            roleChangeRepository.save(roleChange);
        }

        patientRegistration.setRegistered(true);
        patientRegistrationRepository.save(patientRegistration);
        return ApprovePatientRegistrationResponseFactory.Successful(patientRegistration.getId());
    }


    public List<PatientRegistrationData> getPatientRegistrations(String username) {
        UserModel userModel = userService.findByLogin(username);
        List<PatientRegistration> patientRegistrations = new ArrayList<>();
        if (userModel.getRole().equals(USERROLE.PRACTITIONER)) {
            patientRegistrations = patientRegistrationRepository.findByPractitionerUserModel(userModel);
        }
        if (userModel.getRole().equals(USERROLE.PATIENT) || userModel.getRole().equals(USERROLE.USER)) {
            patientRegistrations = patientRegistrationRepository.findByUserModel(userModel);
        }
        return patientRegistrations.stream().map((PatientRegistrationDataFactory::From
        )).toList();
    }

}
