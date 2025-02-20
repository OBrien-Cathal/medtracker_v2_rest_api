package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.exception.validation.medication.MedicationValidationException;
import com.cathalob.medtracker.factory.AccountDetailsFactory;
import com.cathalob.medtracker.model.AccountDetails;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.repository.AccountDetailsRepository;
import com.cathalob.medtracker.repository.PatientRegistrationRepository;
import com.cathalob.medtracker.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class AccountDetailsService {
    private final UserService userService;
    private final AccountDetailsFactory accountDetailsFactory;
    private final AccountDetailsRepository accountDetailsRepository;
    private final PatientRegistrationRepository patientRegistrationRepository;

    public AccountDetails getDetails(String username) {
        UserModel userModel = userService.findByLogin(username);
        return accountDetailsRepository.findById(userModel.getId()).orElse(null);
    }

    public Long updateAccountDetails(String username, String firstName, String surname) {
        UserModel userModel = userService.findByLogin(username);

        AccountDetails accountDetails = accountDetailsRepository.findById(userModel.getId()).orElse(null);

        if(accountDetails == null) throw new MedicationValidationException(List.of("No account details found"));
        accountDetails.setFirstName(firstName);
        accountDetails.setSurname(surname);
        return accountDetailsRepository.save(accountDetails).getUserModel().getId();
    }

    public Long addAccountDetails(UserModel userModel) {
        return accountDetailsRepository.save(accountDetailsFactory.newAccountDetails(userModel)).getUserModel().getId();
    }

    public AccountDetails getDetails(String name, @NotNull Long patientId) {
        UserModel practitioner = userService.findByLogin(name);
        UserModel patient = userService.findUserModelById(patientId).orElse(null);

        if (patient == null) throw new MedicationValidationException(List.of("Patient not found with ID: " + patientId));

        if (patientRegistrationRepository.findByUserModelAndPractitionerUserModel(patient, practitioner).isEmpty()) {
            throw new MedicationValidationException(List.of("Only registered practitioners can view this patients data"));
        }

        return accountDetailsRepository.findById(patient.getId()).orElse(null);
    }
}
