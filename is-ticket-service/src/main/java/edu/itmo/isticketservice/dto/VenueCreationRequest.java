package edu.itmo.isticketservice.dto;

import edu.itmo.isticketservice.model.VenueType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class VenueCreationRequest {

    @NotBlank
    private String name;

    @Positive
    private long capacity;

    @NotNull
    private VenueType type;

}
