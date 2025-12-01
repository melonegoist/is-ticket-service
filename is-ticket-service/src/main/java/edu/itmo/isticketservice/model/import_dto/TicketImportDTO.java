package edu.itmo.isticketservice.model.import_dto;

import edu.itmo.isticketservice.model.Coordinates;
import edu.itmo.isticketservice.model.TicketType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TicketImportDTO {

    @NotBlank(message = "name is required")
    private String name;

    @Valid
    @NotNull(message = "coordinates are required")
    private Coordinates coordinates;

    @Valid
    @NotNull(message = "person is required")
    private PersonImportDTO person;

    @Valid
    @NotNull(message = "venue is required")
    private VenueImportDTO venue;

    @NotNull(message = "price is required")
    @Positive(message = "price must be positive")
    private Integer price;

    private TicketType ticketType;

    @NotNull(message = "discount is required")
    @Min(value = 1, message = "discount must be at least 1")
    @Max(value = 100, message = "discount must be at most 100")
    private Integer discount;

    @Positive(message = "number of tickets must be positive")
    private Integer number;

}
