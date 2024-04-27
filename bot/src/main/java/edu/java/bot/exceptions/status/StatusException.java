package edu.java.bot.exceptions.status;

import org.springframework.http.HttpStatus;

public abstract class StatusException extends RuntimeException {

    public abstract HttpStatus getStatus();

}
