package com.cathalob.medtracker.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "SIGN_IN_RECORD")
@Data
@IdClass(SignInRecordId.class)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignInRecord {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USERMODEL_ID", nullable = false)
    @JsonIgnore
    private UserModel userModel;

    @Id
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime signInTime;

}
