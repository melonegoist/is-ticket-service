package edu.itmo.isticketservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SellTicketRequest {

    @NotNull
    @Positive(message = "Sale price must be positive")
    private Integer salePrice;

    @NotBlank
    private String personId;

}
