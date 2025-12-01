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
    @AttributeOverride(name = "x", column = @Column(name = "coord_x"))
    @AttributeOverride(name = "y", column = @Column(name = "coord_y", nullable = false))
    private Coordinates coordinates;

    @NotNull
    @Column(nullable = false)
    @Builder.Default
    private LocalDate creationDate = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne // todo: lazy fetch type
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

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
