package edu.itmo.isticketservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {

    @NotBlank(message = "Field \"username\" must be set!")
    @Size(min = 5, max = 20)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank(message = "Field \"password\" must be set!")
    @Size(min = 8, max = 20)
    private String password;

}
