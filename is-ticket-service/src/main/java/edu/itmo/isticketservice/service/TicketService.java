package edu.itmo.isticketservice.service;

import edu.itmo.isticketservice.dto.*;
import edu.itmo.isticketservice.model.*;
import edu.itmo.isticketservice.model.import_dto.PersonImportDTO;
import edu.itmo.isticketservice.model.import_dto.TicketImportDTO;
import edu.itmo.isticketservice.model.import_dto.VenueImportDTO;
import edu.itmo.isticketservice.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private static final String TICKET_NOT_FOUND = "Ticket not found";
    public static final String USER_NOT_FOUND = "User not found: ";
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final VenueRepository venueRepository;
    private final PersonRepository personRepository;
    private final Validator validator;

    public Ticket createTicket(TicketCreationRequest request, String username) {
        Person person = personRepository.findPersonByPassportID(String.valueOf(request.getPersonId()))
                .orElseThrow(() -> new EntityNotFoundException("Owner not found " + request.getPersonId()));

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new EntityNotFoundException("Venue not found " + request.getVenueId()));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found " + username));

        Ticket ticket = Ticket.builder()
                .name(request.getName())
                .coordinates(request.getCoordinates())
                .person(person)
                .venue(venue)
                .price(request.getPrice())
                .type(request.getTicketType())
                .discount(request.getDiscount())
                .number(request.getNumber())
                .user(user)
                .build();

        Ticket ticketCreated = ticketRepository.save(ticket);

        log.info("Ticket created with Id: {} by user: {}", ticketCreated.getId(), username);

        return ticketCreated;
    }

    public Page<TicketCreationResponse> getAllTickets(Pageable pageable, String substring) {
        return ticketRepository.findAll(pageable)
                .map(ticket -> this.convertToResponse(ticket, substring));
    }

    public TicketCreationResponse getTicketById(Integer id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TICKET_NOT_FOUND));

        return convertToResponse(ticket);
    }

    public Ticket updateTicket(Integer id, TicketCreationRequest request, String username) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TICKET_NOT_FOUND));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found" + username));

        if (!user.getRole().equals(Role.ROLE_ADMIN) && !ticket.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Access denied");
        }

        Person person = personRepository.findPersonByPassportID(String.valueOf(request.getPersonId()))
                .orElseThrow(() -> new EntityNotFoundException("Owner not found" + request.getPersonId()));

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new EntityNotFoundException("Venue not found" + request.getVenueId()));

        ticket.setName(request.getName());
        ticket.setCoordinates(request.getCoordinates());
        ticket.setPerson(person);
        ticket.setVenue(venue);
        ticket.setPrice(request.getPrice());
        ticket.setDiscount(request.getDiscount());
        ticket.setType(request.getTicketType());
        ticket.setNumber(request.getNumber());

        Ticket updatedTicket = ticketRepository.save(ticket);
        log.info("Ticket updated with Id: {} by user: {}", updatedTicket.getId(), username);

        return updatedTicket;
    }

    public void deleteTicket(Integer id, String username) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TICKET_NOT_FOUND));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found" + username));

        if (!user.getRole().equals(Role.ROLE_ADMIN) && !ticket.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Access denied");
        }

        ticketRepository.delete(ticket);
        log.info("Ticket deleted with Id: {} by user: {}", id, username);
    }

    public List<Ticket> findTicketsByNameContaining(String substring) {
        if (substring == null || substring.trim().isEmpty()) {
            throw new IllegalArgumentException("Substring cannot be null or empty");
        }

        return ticketRepository.findByNameContainingIgnoreCase(substring.trim());
    }

    public List<Ticket> findTicketsByNameStartsWith(String substring) {
        if (substring == null || substring.trim().isEmpty()) {
            throw new IllegalArgumentException("Substring cannot be null or empty");
        }
        return ticketRepository.findByNameStartingWithIgnoreCase(substring.trim());
    }

    public List<Ticket> findTicketsByNumberLessThan(Integer number) {
        if (number == null || number <= 0) {
            throw new IllegalArgumentException("Number must be positive");
        }
        return ticketRepository.findByNumberLessThan(number);
    }

    public List<Ticket> findTicketsByNumberGreaterThan(Integer number) {
        if (number == null || number <= 0) {
            throw new IllegalArgumentException("Number must be positive");
        }
        return ticketRepository.findByNumberGreaterThan(number);
    }

    public int getSumOfTicketsNumber() {
        if (ticketRepository.findAll().isEmpty()) {
            return 0;
        } else {
            return ticketRepository.findAll().stream().mapToInt(Ticket::getNumber).sum();
        }
    }

    public TicketCreationResponse cloneTicketWithDiscount(Integer ticketId, CloneTicketRequest request, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND + username));

        Ticket originalTicket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + ticketId));

        int discountAmount = (originalTicket.getPrice() * request.getDiscount()) / 100;
        int newPrice = originalTicket.getPrice() - discountAmount;
        int newDiscount = request.getDiscount();

        Ticket clonedTicket = Ticket.builder()
                .name(originalTicket.getName() + " (Clone)")
                .coordinates(originalTicket.getCoordinates())
                .creationDate(LocalDate.now())
                .person(originalTicket.getPerson())
                .event(originalTicket.getEvent())
                .price(newPrice)
                .type(originalTicket.getType())
                .discount(newDiscount)
                .number(originalTicket.getNumber())
                .venue(originalTicket.getVenue())
                .user(currentUser)
                .build();

        Ticket savedTicket = ticketRepository.save(clonedTicket);
        return convertToResponse(savedTicket);
    }

    public TicketCreationResponse sellTicket(Integer ticketId, SellTicketRequest request, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND + username));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + ticketId));

        Person person = personRepository.findById(String.valueOf(request.getPersonId()))
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + request.getPersonId()));

        // Проверяем права доступа
        if (!currentUser.getRole().equals(Role.ROLE_ADMIN) &&
                !ticket.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only sell tickets created by you");
        }

        // Проверяем, что цена продажи положительная
        if (request.getSalePrice() <= 0) {
            throw new IllegalArgumentException("Sale price must be positive");
        }

        // Обновляем билет
        ticket.setPrice(request.getSalePrice());
        ticket.setPerson(person); // Привязываем нового человека
        ticket.setCreationDate(LocalDate.now()); // Обновляем дату создания

        Ticket updatedTicket = ticketRepository.save(ticket);
        return convertToResponse(updatedTicket);
    }

    private TicketCreationResponse convertToResponse(Ticket ticket) {
        return new TicketCreationResponse(
                ticket.getId(),
                ticket.getName(),
                ticket.getCoordinates(),
                ticket.getCreationDate(),
                ticket.getPerson().getPassportID(),
                ticket.getVenue().getId(),
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

    private TicketCreationResponse convertToResponse(Ticket ticket, String substring) {
        if (!ticket.getName().contains(substring)) {
            return null;
        } else {
            return convertToResponse(ticket);
        }
    }

    @Transactional
    public void deleteTicketsByVenueId(Long venueId) {
        if (venueId != null) {
            ticketRepository.deleteByVenue_Id(venueId);
        }
    }

    @Transactional
    public List<TicketCreationResponse> importTickets(List<TicketImportDTO> ticketImportRequests, String username) {
        validateImportPayload(ticketImportRequests);

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND + username));

        List<Ticket> createdTickets = ticketImportRequests.stream()
                .map(request -> buildTicketFromImport(request, currentUser))
                .map(ticketRepository::save)
                .toList();

        return createdTickets.stream()
                .map(this::convertToResponse)
                .toList();
    }

    private void validateImportPayload(List<TicketImportDTO> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            throw new IllegalArgumentException("Ticket import payload must contain at least one ticket");
        }

        List<ConstraintViolation<TicketImportDTO>> violations = tickets.stream()
                .map(dto -> validator.validate(dto))
                .flatMap(Collection::stream)
                .toList();


        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("validation failed for imported tickets", new HashSet<>(violations));
        }
    }

    private Ticket buildTicketFromImport(TicketImportDTO request, User currentUser) {
        Person person = resolvePerson(request.getPerson());
        Venue venue = resolveVenue(request.getVenue());

        return Ticket.builder()
                .name(request.getName())
                .coordinates(request.getCoordinates())
                .person(person)
                .venue(venue)
                .price(request.getPrice())
                .type(request.getTicketType())
                .discount(request.getDiscount())
                .number(request.getNumber())
                .user(currentUser)
                .build();
    }

    private Person resolvePerson(PersonImportDTO personRequest) {
        return personRepository.findPersonByPassportID(personRequest.getPassportID())
                .map(existing -> {
                    boolean locationMismatch = (existing.getLocation() == null && personRequest.getLocation() != null)
                            || (existing.getLocation() != null && !existing.getLocation().equals(personRequest.getLocation()));

                    if (!existing.getEyeColor().equals(personRequest.getEyeColor())
                            || existing.getHairColor() != personRequest.getHairColor()
                            || !existing.getNationality().equals(personRequest.getNationality())
                            || locationMismatch) {
                        throw new IllegalArgumentException("Person with passport ID " + personRequest.getPassportID()
                                + " already exists with different data");
                    }

                    return existing;
                })
                .orElseGet(() -> personRepository.save(Person.builder()
                        .passportID(personRequest.getPassportID())
                        .eyeColor(personRequest.getEyeColor())
                        .hairColor(personRequest.getHairColor())
                        .location(personRequest.getLocation())
                        .nationality(personRequest.getNationality())
                        .build()));
    }

    private Venue resolveVenue(VenueImportDTO venueRequest) {
        if (venueRequest.getId() != null) {
            return venueRepository.findVenueById(venueRequest.getId())
                    .map(existing -> {
                        if (!existing.getName().equals(venueRequest.getName())
                                || !existing.getType().equals(venueRequest.getVenueType())) {
                            throw new IllegalArgumentException("Venue with ID " + venueRequest.getId()
                                    + " already exists with different data");
                        }
                        return existing;
                    })
                    .orElseThrow(() -> new EntityNotFoundException("Venue not found" + venueRequest.getId()));
        }

        return venueRepository.save(Venue.builder()
                .name(venueRequest.getName())
                .capacity(venueRequest.getCapacity())
                .type(venueRequest.getVenueType())
                .build());
    }

}
