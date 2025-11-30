package edu.itmo.isticketservice.service;

import edu.itmo.isticketservice.dto.VenueCreationRequest;
import edu.itmo.isticketservice.dto.VenueCreationResponse;
import edu.itmo.isticketservice.model.Venue;
import edu.itmo.isticketservice.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;
    private final TicketService ticketService;

    public VenueCreationResponse createVenue(VenueCreationRequest request) {
        Venue venue = Venue.builder()
                .name(request.getName())
                .type(request.getType())
                .capacity(request.getCapacity())
                .build();

        venueRepository.save(venue);

        return toDto();
    }

    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    private VenueCreationResponse toDto() {
        return new VenueCreationResponse(
                "Venue created successfully"
        );
    }

    @Transactional
    public void deleteVenue(Long id) {
        if (venueRepository.existsById(id)) {
            ticketService.deleteTicketsByVenueId(id);
            venueRepository.deleteById(id);
        }
    }

}
