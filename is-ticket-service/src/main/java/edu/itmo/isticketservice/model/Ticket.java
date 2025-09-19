package edu.itmo.isticketservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @Size(min = 1)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "coord_x")),
            @AttributeOverride(name = "y", column = @Column(name = "coord_y", nullable = false))

    })
    private Coordinates coordinates;

    @NotNull
    @Column(nullable = false)
    @Builder.Default
    private LocalDate creationDate = LocalDate.now();

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    private TicketType type;

    @Min(1)
    @Max(100)
    private int discount;

    @Positive
    private Integer number;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

}
