package edu.itmo.isticketservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "venues")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Positive
    @Column
    private long capacity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VenueType venueType;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL,  fetch = FetchType.LAZY) // Many venues - one address == each venue has one address, but each address may have a lot of venues on it
    @JoinColumn(name = "address_id",  nullable = false)
    private Address address;

}
