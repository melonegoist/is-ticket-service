package edu.itmo.isticketservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @NotNull
    private Long x;

    @NotNull
    private Float y;

    @NotNull
    private Double z;

}
