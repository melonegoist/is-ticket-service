package edu.itmo.isticketservice.dto;

import edu.itmo.isticketservice.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TicketCreationResponse {

    private Integer id;
    private String name;
    private Coordinates coordinates;
    private LocalDate created;
    private Person person;
    private Event event;
    private Integer price;
    private TicketType ticketType;
    private int discount;
    private Integer number;
    private Venue venue;
    private String createdBy;
    private LocalDate lastModified;

}
