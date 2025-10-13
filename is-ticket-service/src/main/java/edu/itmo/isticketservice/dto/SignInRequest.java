package edu.itmo.isticketservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignInRequest {

    @NotBlank(message = "Username or email is required!")
    private String login;

    @NotBlank(message = "Password is required!")
    private String password;

}
