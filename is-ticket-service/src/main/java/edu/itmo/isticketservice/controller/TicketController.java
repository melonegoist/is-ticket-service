package edu.itmo.isticketservice.controller;

import edu.itmo.isticketservice.dto.CloneTicketRequest;
import edu.itmo.isticketservice.dto.SellTicketRequest;
import edu.itmo.isticketservice.dto.TicketCreationRequest;
import edu.itmo.isticketservice.dto.TicketCreationResponse;
import edu.itmo.isticketservice.model.Ticket;
import edu.itmo.isticketservice.service.TicketService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Validated
public class TicketController {

    public static final String TOPIC_TICKETS = "/topic/tickets";
    private final TicketService ticketService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/create-ticket")
    public ResponseEntity<TicketCreationResponse> createTicket(
            @Valid @RequestBody TicketCreationRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Ticket ticket = ticketService.createTicket(request, userDetails.getUsername());
        simpMessagingTemplate.convertAndSend(TOPIC_TICKETS, ticket);

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(ticket));
    }

    @GetMapping
    public ResponseEntity<Page<TicketCreationResponse>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String substring
    ) {
        String sortField = sort.split(",")[0];
        boolean sortAscending = sort.split(",").length == 1 || sort.split(",")[1].equals("asc");

        Pageable pageable;

        if (sortAscending) {
            pageable = PageRequest.of(page, size, Sort.by(sortField).ascending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by(sortField).descending());
        }

        Page<TicketCreationResponse> tickets = ticketService.getAllTickets(pageable, Objects.requireNonNullElse(substring, ""));

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketCreationResponse> getTicket(@PathVariable Integer id) {
        TicketCreationResponse ticket = ticketService.getTicketById(id);

        return ResponseEntity.ok(ticket);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketCreationResponse> updateTicket(
            @PathVariable Integer id,
            @Valid @RequestBody TicketCreationRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Ticket updatedTicket = ticketService.updateTicket(id, request, userDetails.getUsername());
        simpMessagingTemplate.convertAndSend(TOPIC_TICKETS, updatedTicket);

        return ResponseEntity.ok(convertToResponse(updatedTicket));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ticketService.deleteTicket(id, userDetails.getUsername());
        simpMessagingTemplate.convertAndSend(TOPIC_TICKETS, id);

        return ResponseEntity.noContent().build();
    }

    // 1. Вернуть массив объектов, значение поля name которых содержит заданную подстроку
    @GetMapping("/search/name-contains")
    public ResponseEntity<List<Ticket>> findTicketsByNameContains(
            @RequestParam @NotBlank String substring
    ) {
        List<Ticket> tickets = ticketService.findTicketsByNameContaining(substring);

        return ResponseEntity.ok(tickets);
    }

    // 2. Вернуть массив объектов, значение поля name которых начинается с заданной подстроки
    @GetMapping("/search/name-starts-with")
    public ResponseEntity<List<Ticket>> findTicketsByNameStartsWith(
            @RequestParam @NotBlank String prefix
    ) {
        List<Ticket> tickets = ticketService.findTicketsByNameStartsWith(prefix);

        return ResponseEntity.ok(tickets);
    }

    // 3. Вернуть массив объектов, значение поля number которых меньше заданного
    @GetMapping("/search/number-less-than")
    public ResponseEntity<List<Ticket>> findTicketsByNumberLessThan(
            @RequestParam @Positive Integer number
    ) {
        List<Ticket> tickets = ticketService.findTicketsByNumberLessThan(number);

        return ResponseEntity.ok(tickets);
    }

    // 4. Вернуть массив объектов, значение поля number которых больше заданного
    @GetMapping("/search/number-greater-than")
    public ResponseEntity<List<Ticket>> findTicketsByNumberGreaterThan(
            @RequestParam @Positive Integer number
    ) {
        List<Ticket> tickets = ticketService.findTicketsByNumberGreaterThan(number);

        return ResponseEntity.ok(tickets);
    }

    // 5. Создать новый билет на основе указанного, указав скидку в заданное число %,
    // и, одновременно, увеличив цену билета на ту же самую сумму
    @PostMapping("/{ticketId}/clone-with-discount")
    public ResponseEntity<TicketCreationResponse> cloneTicketWithDiscount(
            @PathVariable Integer ticketId,
            @RequestBody @Valid CloneTicketRequest request,
            Principal principal
    ) {
        TicketCreationResponse clonedTicket = ticketService.cloneTicketWithDiscount(ticketId, request, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(clonedTicket);
    }

    // 6. Посчитать сумму количества всех билетов
    @GetMapping("/search/get-tickets-number-sum")
    public ResponseEntity<Integer> getTicketsNumberSum() {
        return ResponseEntity.ok(ticketService.getSumOfTicketsNumber());
    }

    // 7. Продать билет
    @PostMapping("/{ticketId}/sell")
    public ResponseEntity<TicketCreationResponse> sellTicket(
            @PathVariable Integer ticketId,
            @RequestBody @Valid SellTicketRequest request,
            Principal principal
    ) {
        TicketCreationResponse soldTicket = ticketService.sellTicket(ticketId, request, principal.getName());

        return ResponseEntity.ok(soldTicket);
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

}
