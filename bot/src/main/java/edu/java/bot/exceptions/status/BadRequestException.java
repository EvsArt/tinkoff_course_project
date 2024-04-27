package edu.java.bot.exceptions.status;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class BadRequestException extends StatusException {

    @Getter
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

}
