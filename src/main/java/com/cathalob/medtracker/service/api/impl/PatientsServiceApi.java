package com.cathalob.medtracker.service.api.impl;

import com.cathalob.medtracker.exception.validation.PatientRegistrationException;
import com.cathalob.medtracker.fileupload.BloodPressureFileImporter;
import com.cathalob.medtracker.fileupload.DoseFileImporter;
import com.cathalob.medtracker.model.PatientRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import com.cathalob.medtracker.model.factories.PatientRegistrationFactory;
import com.cathalob.medtracker.model.tracking.BloodPressureReading;
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
import com.cathalob.medtracker.service.impl.BloodPressureDataService;
import com.cathalob.medtracker.service.impl.DoseService;
import com.cathalob.medtracker.service.impl.EvaluationDataService;
import com.cathalob.medtracker.service.impl.PrescriptionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final PrescriptionsService prescriptionsService;
    private final BloodPressureDataService bloodPressureDataService;
    private final DoseService doseService;
    private final EvaluationDataService evaluationDataService;

    //    @PreAuthorize("hasRole('ROLE_PRACTITIONER')")
    public List<UserModel> getPatientUserModelsForPractitioner(String username) {
        UserModel userModel = userService.findByLogin(username);
        if (userModel == null || !userModel.getRole().equals(USERROLE.PRACTITIONER)) return List.of();
        return patientRegistrationRepository.findByPractitionerUserModel(userModel)
                .stream()
                .map((PatientRegistration::getUserModel)).toList();
    }


    public PatientRegistrationResponse registerPatient(String username, Long practitionerId) {
        UserModel toRegister = userService.findByLogin(username);
        Optional<UserModel> maybePractitioner = userService.findUserModelById(practitionerId);

        UserModel practitioner = maybePractitioner.orElse(null);
        try {
            validatePatientRegistration(toRegister, practitioner);
        } catch (Exception e) {
            return PatientRegistrationResponseFactory.Failed(List.of(e.getMessage()));
        }

        PatientRegistration patientRegistration = PatientRegistrationFactory.PatientRegistration(toRegister, practitioner, false);
        PatientRegistration saved = patientRegistrationRepository.save(patientRegistration);

        return PatientRegistrationResponseFactory.Successful(saved);
    }

    private void validatePatientRegistration(UserModel toRegister, UserModel practitioner) {
        if (practitioner == null) {
            throw new PatientRegistrationException("Practitioner does not exist");
        }
        if (!List.of(USERROLE.USER, USERROLE.PATIENT).contains(toRegister.getRole())) {
            throw new PatientRegistrationException("User does not have allowed role to register as a patient (allowed: USER, PATIENT), current: " +
                    toRegister.getRole());
        }
        List<PatientRegistration> existingReg = patientRegistrationRepository.findByUserModelAndPractitionerUserModel(toRegister, practitioner);
        if (!existingReg.isEmpty()) {
            throw new PatientRegistrationException("Registration for practitioner and patient already exists");
        }
    }


    public ApprovePatientRegistrationResponse approvePatientRegistration(String username, Long patientRegistrationId) {
        UserModel shouldBePractitionerUserModel = userService.findByLogin(username);
        Optional<PatientRegistration> reg = patientRegistrationRepository.findById(patientRegistrationId);

        ArrayList<String> errors = new ArrayList<>();
        if (shouldBePractitionerUserModel == null) {
            errors.add("Only users with PRACTITIONER role can approve patient registrations");
            return ApprovePatientRegistrationResponseFactory.Failed(errors);
        }
        if (reg.isEmpty()) {
            errors.add("Registration with id " + patientRegistrationId + " does not exist");
            return ApprovePatientRegistrationResponseFactory.Failed(errors);
        }
        PatientRegistration patientRegistration = reg.get();
        if (!shouldBePractitionerUserModel.getId().equals(patientRegistration.getPractitionerUserModel().getId())) {
            errors.add("Approval of requests for other users not allowed");
            return ApprovePatientRegistrationResponseFactory.Failed(errors);
        }
        if (patientRegistration.isRegistered()) {
            errors.add("Registration is already approved for reg id: " + patientRegistrationId);
            return ApprovePatientRegistrationResponseFactory.Failed(errors);
        }
        if (!List.of(USERROLE.USER, USERROLE.PATIENT).contains(patientRegistration.getUserModel().getRole())) {
            errors.add("Cannot approve registration of user with role: " + patientRegistration.getUserModel().getRole());
            return ApprovePatientRegistrationResponseFactory.Failed(errors);
        }

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

    public void importDoseFile(MultipartFile file, String username) {
        new DoseFileImporter(
                userService.findByLogin(username),
                evaluationDataService,
                prescriptionsService,
                doseService)
                .processMultipartFile(file);
    }

    public void importBloodPressureFile(MultipartFile file, String username) {
        new BloodPressureFileImporter(
                userService.findByLogin(username),
                evaluationDataService,
                this)
                .processMultipartFile(file);
    }
    public void saveBloodPressureReadings(List<BloodPressureReading> bloodPressureReadings) {
        bloodPressureDataService.saveBloodPressureReadings(bloodPressureReadings);
    }

}
