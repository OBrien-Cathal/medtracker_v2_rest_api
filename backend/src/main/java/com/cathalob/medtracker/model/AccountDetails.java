package com.cathalob.medtracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "ACCOUNT_DETAILS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetails {
    @Id
    private Long id;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USERMODEL_ID", nullable = false)
    @JsonIgnore
    private UserModel userModel;

    private String firstName;

    private String surname;

}
