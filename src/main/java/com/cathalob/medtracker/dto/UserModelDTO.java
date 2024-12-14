package com.cathalob.medtracker.dto;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModelDTO {

    @Email(message = "Invalid email")
    private String username;
    private String password;


}
