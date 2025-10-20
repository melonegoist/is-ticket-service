package edu.itmo.isticketservice.dto;

import edu.itmo.isticketservice.model.Color;
import edu.itmo.isticketservice.model.Country;
import edu.itmo.isticketservice.model.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PersonCreationRequest {

    @NotBlank
    private String passportID;

    @NotNull
    private LocalDateTime birthday;

    @NotNull
    private Color eyeColor;

    @NotNull
    private Color hairColor;

    @NotNull
    private Location location;

    @Positive
    private Float height;

    @NotNull
    private Country nationality;

}
