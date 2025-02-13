package com.cathalob.medtracker.repository;


import com.cathalob.medtracker.model.AccountRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRegistrationRepository extends JpaRepository<AccountRegistration, Long> {

    List<AccountRegistration> findByUserModelIdAndConfirmedAndRegistrationId(Long userModelId, boolean confirmed, UUID registrationId);
    List<AccountRegistration> findByUserModelId(Long userModelId);
    List<AccountRegistration> findByUserModelIdAndConfirmed(Long userModelId, boolean confirmed);
}
