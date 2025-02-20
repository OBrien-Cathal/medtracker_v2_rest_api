package com.cathalob.medtracker.repository;

import com.cathalob.medtracker.model.SignInRecord;
import com.cathalob.medtracker.model.SignInRecordId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignInRecordsRepository extends JpaRepository<SignInRecord, SignInRecordId>{


}
