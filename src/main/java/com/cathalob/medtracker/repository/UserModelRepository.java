package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.UserModel;
import com.cathalob.medtracker.model.enums.USERROLE;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserModelRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByUsername(String username);
    List<UserModel> findByRole(USERROLE role);

}
