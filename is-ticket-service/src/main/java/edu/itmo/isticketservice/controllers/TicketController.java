package edu.itmo.isticketservice.controllers;

import edu.itmo.isticketservice.dto.CloneTicketRequest;
import edu.itmo.isticketservice.dto.SellTicketRequest;
import edu.itmo.isticketservice.dto.TicketCreationRequest;
import edu.itmo.isticketservice.dto.TicketCreationResponse;
import edu.itmo.isticketservice.model.Ticket;
import edu.itmo.isticketservice.services.TicketService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Validated
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/create-ticket")
    public ResponseEntity<TicketCreationResponse> createTicket(
            @Valid @RequestBody TicketCreationRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        TicketCreationResponse response = ticketService.createTicket(request, userDetails.getUsername());

        System.out.println("CHECKPOINT 1");
        System.out.println(request.toString());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
        TicketCreationResponse response = ticketService.updateTicket(id, request, userDetails.getUsername());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ticketService.deleteTicket(id, userDetails.getUsername());

        return ResponseEntity.noContent().build();
    }

    // 1. Вернуть массив объектов, значение поля name которых содержит заданную подстроку
    @GetMapping("/search/name-contains")
    public ResponseEntity<List<Ticket>> findTicketsByNameContains(
            @RequestParam @NotBlank String substring) {
        List<Ticket> tickets = ticketService.findTicketsByNameContaining(substring);
        return ResponseEntity.ok(tickets);
    }

    // 2. Вернуть массив объектов, значение поля name которых начинается с заданной подстроки
    @GetMapping("/search/name-starts-with")
    public ResponseEntity<List<Ticket>> findTicketsByNameStartsWith(
            @RequestParam @NotBlank String prefix) {
        List<Ticket> tickets = ticketService.findTicketsByNameStartsWith(prefix);
        return ResponseEntity.ok(tickets);
    }

    // 3. Вернуть массив объектов, значение поля number которых меньше заданного
    @GetMapping("/search/number-less-than")
    public ResponseEntity<List<Ticket>> findTicketsByNumberLessThan(
            @RequestParam @Positive Integer number) {
        List<Ticket> tickets = ticketService.findTicketsByNumberLessThan(number);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/search/number-greater-than")
    public ResponseEntity<List<Ticket>> findTicketsByNumberGreaterThan(
            @RequestParam @Positive Integer number) {
        List<Ticket> tickets = ticketService.findTicketsByNumberGreaterThan(number);
        return ResponseEntity.ok(tickets);
    }

    // 5. Создать новый билет на основе указанного, указав скидку в заданное число %,
    // и, одновременно, увеличив цену билета на ту же самую сумму
    @PostMapping("/{ticketId}/clone-with-discount")
    public ResponseEntity<TicketCreationResponse> cloneTicketWithDiscount(
            @PathVariable Integer ticketId,
            @RequestBody @Valid CloneTicketRequest request,
            Principal principal) {
        TicketCreationResponse clonedTicket = ticketService.cloneTicketWithDiscount(ticketId, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(clonedTicket);
    }

    @GetMapping("/search/get-tickets-number-sum")
    public ResponseEntity<Integer> getTicketsNumberSum() {
        return ResponseEntity.ok(ticketService.getSumOfTicketsNumber());
    }

    @PostMapping("/{ticketId}/sell")
    public ResponseEntity<TicketCreationResponse> sellTicket(
            @PathVariable Integer ticketId,
            @RequestBody @Valid SellTicketRequest request,
            Principal principal) {
        TicketCreationResponse soldTicket = ticketService.sellTicket(ticketId, request, principal.getName());
        return ResponseEntity.ok(soldTicket);
    }

}
