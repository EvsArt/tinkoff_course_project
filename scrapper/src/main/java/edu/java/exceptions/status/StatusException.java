package edu.java.exceptions.status;

import org.springframework.http.HttpStatus;

public abstract class StatusException extends RuntimeException {

    public abstract HttpStatus getStatus();

}
