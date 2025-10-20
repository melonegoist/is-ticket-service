package edu.itmo.isticketservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CloneTicketRequest {

    @Min(1)
    @Max(100)
    private int discount;

}
