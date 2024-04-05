package edu.java.exceptions.status;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ServerErrorException extends StatusException {

    @Getter
    private final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

}
