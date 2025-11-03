package edu.itmo.isticketservice.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class VenueNotFoundException extends EntityNotFoundException {

    public VenueNotFoundException(Long id) {
        super("Venue with id " + id + " not found");
    }

}
