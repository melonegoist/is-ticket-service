package edu.itmo.isticketservice.exception;

public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String resourceName, String identifier) {
        super(resourceName + " with identifier " + identifier + " already exists");
    }

}
