package edu.itmo.isticketservice.exception;

import jakarta.persistence.EntityNotFoundException;

public class EventNotFoundException extends EntityNotFoundException {

    public EventNotFoundException(Long id) {
        super("Event with id " + id + " not found");
    }

}
