package com.cathalob.medtracker.mapper;

import com.cathalob.medtracker.model.SignInRecord;
import com.cathalob.medtracker.payload.data.SignInRecordData;

import java.util.List;

public class SignInRecordsMapper {

    public List<SignInRecordData> signInRecordDataList(List<SignInRecord> records) {

        return records.stream().map(signInRecord -> SignInRecordData.builder()
                .username(signInRecord.getUserModel().getUsername())
                .userModelId(signInRecord.getUserModel().getId())
                .signInTime(signInRecord.getSignInTime()).build()).sorted((o1, o2) -> {

            if (o1.getSignInTime().isEqual(o2.getSignInTime())) {
                return 0;
            }
            return o1.getSignInTime().isBefore(o2.getSignInTime()) ? 1 : -1;
        }).toList();

    }

}
