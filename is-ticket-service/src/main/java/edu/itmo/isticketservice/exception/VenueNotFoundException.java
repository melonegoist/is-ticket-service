package edu.itmo.isticketservice.exception;

public class VenueNotFoundException extends RuntimeException {

    public VenueNotFoundException(Long id) {
        super("Venue with id " + id + " not found");
    }

}
