package com.cathalob.medtracker.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
public class SignInRecordId implements Serializable {
    private Long userModel;
    private LocalDateTime signInTime;

    public SignInRecordId(Long userModel, LocalDateTime signInTime) {
        this.userModel = userModel;
        this.signInTime = signInTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignInRecordId that = (SignInRecordId) o;
        return Objects.equals(userModel, that.userModel) && Objects.equals(signInTime, that.signInTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userModel, signInTime);
    }

}
