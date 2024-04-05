package edu.java.bot.scrapperClient.exceptions.status;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class BadRequestException extends StatusException {

    @Getter
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

}
