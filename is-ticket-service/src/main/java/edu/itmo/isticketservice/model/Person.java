package edu.itmo.isticketservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "people")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {

    @Id
    @NotBlank
    @Size(min = 1, max = 43)
    @Column(length = 43, unique = true, nullable = false)
    private String passportID;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Color eyeColor;

    @Enumerated(EnumType.STRING)
    private Color hairColor;

    @Embedded
    private Location location;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Country nationality;

}
