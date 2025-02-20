package com.cathalob.medtracker.service.impl;

import com.cathalob.medtracker.config.client.ClientDetails;
import com.cathalob.medtracker.factory.AccountRegistrationFactory;
import com.cathalob.medtracker.model.AccountRegistration;
import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.repository.AccountRegistrationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class AccountRegistrationService {
    private final AccountRegistrationRepository accountRegistrationRepository;
    private final MailService mailService;
    private final AccountRegistrationFactory accountRegistrationFactory;
    private final ClientDetails clientDetails;


    public boolean confirmRegistration(UUID registrationId, Long userId) {
        List<AccountRegistration> foundList = accountRegistrationRepository.findByUserModelIdAndConfirmedAndRegistrationId(userId,
                false,
                registrationId);

        System.out.println("RegId: " + registrationId);
        System.out.println("Found");
        foundList.forEach(System.out::println);

        if (foundList.isEmpty()) return false;

        AccountRegistration foundReg = foundList.get(0);
        foundReg.setConfirmationTime(LocalDateTime.now());
        foundReg.setConfirmed(true);

        AccountRegistration saved = accountRegistrationRepository.save(foundReg);
        System.out.println("Confirmed @ " + saved.getConfirmationTime());

        return saved.isConfirmed();
    }


    public boolean isUserRegistrationConfirmed(UserModel userModel) {
        return !accountRegistrationRepository.findByUserModelIdAndConfirmed(userModel.getId(), true).isEmpty();
    }

    public boolean registerUserModel(UserModel userModel) {

        List<AccountRegistration> existingRegistrations = accountRegistrationRepository.findByUserModelId(userModel.getId());

        if (!existingRegistrations.isEmpty()) {
            AccountRegistration accountRegistration = existingRegistrations.get(0);

            mailService.sendEmail(accountRegistration.getUserModel().getUsername(),
                    "MedTracker security, unexpected registration attempt",
                    unexpectedRegistrationEmailText(accountRegistration));
            return false;

        }

        AccountRegistration accountRegistration = accountRegistrationFactory.newAccountRegistration(userModel);
        AccountRegistration saved = accountRegistrationRepository.save(accountRegistration);

        if (saved.getId() == null) return false;

        mailService.sendEmail(saved.getUserModel().getUsername(),
                "Welcome to MedTracker, new account registration",
                registrationEmailText(accountRegistration));
        return true;

    }

    private String unexpectedRegistrationEmailText(AccountRegistration accountRegistration) {
        return "You are receiving this email because it has been used in a signup attempt at MedTracker.\n" +
                "If this is a mistake, please take no action.\n\n" +
                (accountRegistration.isConfirmed() ?
                        "Your registration is already confirmed, please sign in"
                        :
                        confirmationLinkText(accountRegistration));
    }

    private String confirmationLinkText(AccountRegistration accountRegistration) {
        return
                "Please click the link below to confirm your registration.\n\n" +
                        getConfirmationUrl(accountRegistration);
    }

    private String registrationEmailText(AccountRegistration accountRegistration) {
        return "You are receiving this email because it has been registered at MedTracker.\n" +
                "If this is a mistake, please take no action.\n" +
                "Otherwise, please click the link below to confirm your registration.\n\n" +
                getConfirmationUrl(accountRegistration);
    }

    private static String userIdParam(Long userModelId) {
        return "/" + userModelId;
    }

    private static String regParam(UUID uuid) {
        return "/" + uuid;
    }

    private String getConfirmationUrlBase() {
        return clientDetails.getWebsiteBaseUrl() + "/account-registration";
    }

    private String getConfirmationUrl(AccountRegistration accountRegistration) {

        return getConfirmationUrlBase() +
                regParam(accountRegistration.getRegistrationId()) +
                userIdParam(accountRegistration.getUserModel().getId());

    }

}
