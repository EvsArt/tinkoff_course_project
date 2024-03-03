package edu.java.bot.api.exceptions;

public class InvalidUpdateException extends RuntimeException {

    public InvalidUpdateException(){
        super();
    }

    public InvalidUpdateException(String message) {
        super(message);
    }

}
