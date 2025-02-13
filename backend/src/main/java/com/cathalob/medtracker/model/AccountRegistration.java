package com.cathalob.medtracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "ACCOUNT_REGISTRATION")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRegistration {
    @Id
    private Long id;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USERMODEL_ID", nullable = false)
    @JsonIgnore
    private UserModel userModel;

    @NotNull
    private boolean confirmed = false;

    private UUID registrationId;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime registrationTime;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime confirmationTime;

}
