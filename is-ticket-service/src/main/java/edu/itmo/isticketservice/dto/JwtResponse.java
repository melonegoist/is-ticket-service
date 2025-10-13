package edu.itmo.isticketservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    @NotBlank
    private String token;

    @NotNull
    @Size(min = 3, max = 3)
    private int status;

    @NotBlank
    private String message;

}
