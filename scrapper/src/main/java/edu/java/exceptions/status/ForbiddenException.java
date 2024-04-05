package edu.java.exceptions.status;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends StatusException {

    @Getter
    private final HttpStatus status = HttpStatus.FORBIDDEN;

}
