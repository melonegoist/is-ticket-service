package edu.itmo.isticketservice.exceptions;

public class InvalidDiscountException extends TicketOperationException {

    public InvalidDiscountException(int discount) {
        super("Invalid discount: " + discount + "%. Must be between 0 and 100.");
    }

}
