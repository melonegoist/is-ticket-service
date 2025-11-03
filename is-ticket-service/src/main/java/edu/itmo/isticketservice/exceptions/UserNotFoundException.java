package edu.itmo.isticketservice.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String username) {
        super("username: " + username);
    }

    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }

}
