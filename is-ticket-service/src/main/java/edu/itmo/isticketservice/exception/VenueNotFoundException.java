package edu.itmo.isticketservice.exception;

import jakarta.persistence.EntityNotFoundException;

public class VenueNotFoundException extends EntityNotFoundException {

    public VenueNotFoundException(Long id) {
        super("Venue with id " + id + " not found");
    }

}
