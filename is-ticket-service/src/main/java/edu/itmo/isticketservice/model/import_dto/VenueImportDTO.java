package edu.itmo.isticketservice.model.import_dto;

import edu.itmo.isticketservice.model.VenueType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class VenueImportDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Positive(message = "Capacity must be positive")
    private long capacity;

    @NotNull(message = "venue type is required")
    private VenueType venueType;

}
