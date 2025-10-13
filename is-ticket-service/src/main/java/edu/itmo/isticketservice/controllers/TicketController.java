package edu.itmo.isticketservice.controllers;

import edu.itmo.isticketservice.dto.TicketCreationRequest;
import edu.itmo.isticketservice.dto.TicketCreationResponse;
import edu.itmo.isticketservice.services.TicketService;
import jakarta.validation.Valid;
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

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TicketCreationResponse>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        Page<TicketCreationResponse> tickets = ticketService.getAllTickets(pageable);

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

    // todo extra from requirements

}
