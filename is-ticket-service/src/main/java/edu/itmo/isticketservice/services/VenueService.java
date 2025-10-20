package edu.itmo.isticketservice.services;

import edu.itmo.isticketservice.dto.VenueCreationRequest;
import edu.itmo.isticketservice.dto.VenueCreationResponse;
import edu.itmo.isticketservice.model.Venue;
import edu.itmo.isticketservice.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;

    public VenueCreationResponse createVenue(VenueCreationRequest request) {
        Venue venue = Venue.builder()
                .name(request.getName())
                .type(request.getType())
                .capacity(request.getCapacity())
                .build();

        venueRepository.save(venue);

        return toDto(venue);
    }

    public List<Venue> getAllVenues() {
        List<Venue> venues = venueRepository.findAll();

        return venues;
    }

    private VenueCreationResponse toDto(Venue venue) {
        return new VenueCreationResponse(
                "Venue created successfully"
        );
    }

}
