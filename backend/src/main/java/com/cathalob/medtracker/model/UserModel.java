package com.cathalob.medtracker.model;

import com.cathalob.medtracker.model.enums.USERROLE;
import jakarta.persistence.*;
import lombok.*;

@Entity(name="USERMODEL")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String username;
    @NonNull
    private String password;

    @Enumerated(EnumType.STRING)
    private USERROLE role;
}
