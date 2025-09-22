package edu.itmo.isticketservice.services;

import edu.itmo.isticketservice.dto.TicketCreationRequest;
import edu.itmo.isticketservice.dto.TicketCreationResponse;
import edu.itmo.isticketservice.model.*;
import edu.itmo.isticketservice.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final PersonRepository personRepository;

    // todo: maybe id better
    public TicketCreationResponse createTicket(TicketCreationRequest request, String username) {
        Person owner = personRepository.findPersonByPassportID(String.valueOf(request.getPersonId()))
                .orElseThrow(() -> new EntityNotFoundException("Owner not found" + request.getPersonId()));

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new EntityNotFoundException("Venue not found" + request.getVenueId()));

        Event event = eventRepository.findById(request.getEventId())
                .orElse(null);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found" + username));

        Ticket ticket = Ticket.builder()
                .name(request.getName())
                .coordinates(request.getCoordinates())
                .person(owner)
                .event(event)
                .venue(venue)
                .price(request.getPrice())
                .type(request.getTicketType())
                .discount(request.getDiscount())
                .number(request.getNumber())
                .user(user)
                .build();

        Ticket ticketCreated = ticketRepository.save(ticket);

        log.info("Ticket created with Id: {} by user: {}", ticketCreated.getId(), username);

        return convertToResponse(ticketCreated);
    }

    public Page<TicketCreationResponse> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    public TicketCreationResponse getTicketById(Integer id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        return convertToResponse(ticket);
    }

    public TicketCreationResponse updateTicket(Integer id, TicketCreationRequest request, String username) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found" + username));

        if (!user.getRole().equals(Role.ROLE_ADMIN) && !ticket.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Access denied");
        }

        Person person = personRepository.findPersonByPassportID(String.valueOf(request.getPersonId()))
                .orElseThrow(() -> new EntityNotFoundException("Owner not found" + request.getPersonId()));

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new EntityNotFoundException("Venue not found" + request.getVenueId()));

        Event event = eventRepository.findById(request.getEventId())
                .orElse(null);

        ticket.setName(request.getName());
        ticket.setCoordinates(request.getCoordinates());
        ticket.setPerson(person);
        ticket.setEvent(event);
        ticket.setVenue(venue);
        ticket.setPrice(request.getPrice());
        ticket.setDiscount(request.getDiscount());
        ticket.setType(request.getTicketType());
        ticket.setNumber(request.getNumber());
//        ticket.setUser(user);

        Ticket updatedTicket = ticketRepository.save(ticket);
        log.info("Ticket updated with Id: {} by user: {}", updatedTicket.getId(), username);

        return convertToResponse(updatedTicket);
    }

    // todo: add some exceptions

    public void deleteTicket(Integer id, String username) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found" + username));

        if (!user.getRole().equals(Role.ROLE_ADMIN) && !ticket.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Access denied");
        }

        ticketRepository.delete(ticket);
        log.info("Ticket deleted with Id: {} by user: {}", id, username);
    }

    // todo add some extra methods from requirements



    private TicketCreationResponse convertToResponse(Ticket ticket) {
        return new TicketCreationResponse(
                ticket.getId(),
                ticket.getName(),
                ticket.getCoordinates(),
                ticket.getCreationDate(),
                ticket.getPerson(),
                ticket.getEvent(),
                ticket.getPrice(),
                ticket.getType(),
                ticket.getDiscount(),
                ticket.getNumber(),
                ticket.getVenue(),
                ticket.getUser().getUsername(),
                LocalDate.now()
        );
    }

}
