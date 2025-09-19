package edu.itmo.isticketservice.services;

import edu.itmo.isticketservice.model.Ticket;
import edu.itmo.isticketservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public Ticket createTicket(Ticket ticket) {
        // todo

        return null;
    }

    public Page<Ticket> getTickets(Pageable pageable) {
        //

        return null;
    }

    public Optional<Ticket> getTicketById(Integer id) {
        //

        return null;
    }

    public Ticket updateTicket(Integer id, Ticket ticketDetails) {
        //

        return null;
    }

    public void deleteTicket(Integer id) {
        //
    }

}
