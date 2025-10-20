package edu.itmo.isticketservice.controllers;

import edu.itmo.isticketservice.dto.VenueCreationRequest;
import edu.itmo.isticketservice.dto.VenueCreationResponse;
import edu.itmo.isticketservice.model.Venue;
import edu.itmo.isticketservice.services.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {
        List<Venue> venues = venueService.getAllVenues();

        return ResponseEntity.ok(venues);
    }

    @PostMapping
    public ResponseEntity<VenueCreationResponse> createVenue(@Valid @RequestBody VenueCreationRequest request) {
        VenueCreationResponse createdVenue = venueService.createVenue(request);

        return ResponseEntity.ok(createdVenue);
    }

}
