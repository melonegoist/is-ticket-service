package edu.itmo.isticketservice.dto;

import edu.itmo.isticketservice.model.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketCreationRequest {

    @NotBlank(message = "name is required!")
    private String name;

    @NotNull(message = "coordinates are required!")
    private Coordinates coordinates;

    private final LocalDateTime created = LocalDateTime.now();

    @NotNull(message = "owner is required!")
    private Long personId;

    private Long eventId;

    @NotNull(message = "price is required!")
    @Positive(message = "price must be positive!")
    private Integer price;

    private TicketType ticketType;

    @Min(value = 1, message = "discount must be in range [1, 100]")
    @Max(value = 100, message = "discount must be in range [1, 100]")
    private int discount;

    @Positive(message = "quantity must be positive!")
    private Integer number;

    @NotNull(message = "venue is required!")
    private Long venueId;

}
