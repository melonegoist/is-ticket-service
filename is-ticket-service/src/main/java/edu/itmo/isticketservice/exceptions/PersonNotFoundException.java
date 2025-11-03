package edu.itmo.isticketservice.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class PersonNotFoundException extends EntityNotFoundException {

    public PersonNotFoundException(String passportId) {
        super("Person with passportId " + passportId + " not found");
    }

}
