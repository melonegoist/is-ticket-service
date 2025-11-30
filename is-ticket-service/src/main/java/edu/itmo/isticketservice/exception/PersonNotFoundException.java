package edu.itmo.isticketservice.exception;

public class PersonNotFoundException extends RuntimeException {

    public PersonNotFoundException(String passportId) {
        super("Person with passportId " + passportId + " not found");
    }

}
