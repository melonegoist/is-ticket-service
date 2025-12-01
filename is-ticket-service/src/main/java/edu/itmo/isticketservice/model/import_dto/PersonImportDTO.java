package edu.itmo.isticketservice.model.import_dto;

import edu.itmo.isticketservice.model.Color;
import edu.itmo.isticketservice.model.Country;
import edu.itmo.isticketservice.model.Location;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PersonImportDTO {

    @NotBlank
    @Size(min = 1, max = 43)
    private String passportID;

    @NotNull
    private Color eyeColor;

    private Color hairColor;

    @Valid
    private Location location;

    @NotNull
    private Country nationality;

}
