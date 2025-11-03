package edu.itmo.isticketservice.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String resourceName, String identifier) {
        super(resourceName + " with identifier " + identifier + " already exists");
    }

}
