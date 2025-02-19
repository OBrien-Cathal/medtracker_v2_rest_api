package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.exception.validation.PatientRegistrationException;
import com.cathalob.medtracker.exception.validation.UserModelValidationException;
import com.cathalob.medtracker.factory.PatientRegistrationFactory;
import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.userroles.RoleChange;

import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.repository.RoleChangeRepository;
import com.cathalob.medtracker.service.UserService;
import com.cathalob.medtracker.validate.service.PatientServiceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class PatientsService {
    private final UserService userService;
    private final RoleChangeRepository roleChangeRepository;
    private final PatientRegistrationRepository patientRegistrationRepository;
    private final PatientServiceValidator validator;
    private final PatientRegistrationFactory factory;

    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public List<UserModel> getPatientUserModelsForPractitioner(String username) {
        UserModel userModel = userService.findByLogin(username);

        try {
            validator.validatePatient(userModel);
        } catch (UserModelValidationException e) {
            return List.of();
        }

        return patientRegistrationRepository.findByPractitionerUserModel(userModel)
                .stream()
                .map((PatientRegistration::getUserModel)).toList();
    }


    public Long registerPatient(String username, Long practitionerId) throws PatientRegistrationException {
        UserModel toRegister = userService.findByLogin(username);
        UserModel practitioner = userService.findUserModelById(practitionerId).orElse(null);

        PatientRegistration patientRegistration = factory.patientRegistration(toRegister, practitioner);

        List<PatientRegistration> existingPatientRegistration =
                patientRegistrationRepository.findByUserModelAndPractitionerUserModel(toRegister, practitioner);
        validator.validateRegisterPatient(
                patientRegistration, existingPatientRegistration.isEmpty() ? null : existingPatientRegistration.get(0));

        return patientRegistrationRepository.save(patientRegistration).getId();
    }


    public Long approvePatientRegistration(String username, Long patientRegistrationId) {
        UserModel shouldBePractitionerUserModel = userService.findByLogin(username);
        PatientRegistration patientRegistration = patientRegistrationRepository.findById(patientRegistrationId).orElse(null);

        validator.validateApprovePatientRegistration(patientRegistration, shouldBePractitionerUserModel);

        if (patientRegistration == null) return null;
        ensureRoleChangeExists(patientRegistration);

        patientRegistration.setRegistered(true);
        patientRegistrationRepository.save(patientRegistration);

        return patientRegistration.getId();
    }

    public List<PatientRegistration> getPatientRegistrations(String username) {
        UserModel userModel = userService.findByLogin(username);

        if (userModel.getRole().equals(USERROLE.PRACTITIONER)) {
         return patientRegistrationRepository.findByPractitionerUserModel(userModel);
        }
        if (userModel.getRole().equals(USERROLE.PATIENT) || userModel.getRole().equals(USERROLE.USER)) {
            return patientRegistrationRepository.findByUserModel(userModel);
        }
        return List.of();
    }


    private void ensureRoleChangeExists(PatientRegistration patientRegistration) {
        if (patientRegistration.getUserModel().getRole().equals(USERROLE.USER)) {
//            Add role change if this is the first time a user has requested a patient registration
            RoleChange roleChange = factory.roleChange(patientRegistration);

            patientRegistration.getUserModel().setRole(USERROLE.PATIENT);

            userService.saveUserModel(patientRegistration.getUserModel());
            roleChangeRepository.save(roleChange);
        }
    }
}
