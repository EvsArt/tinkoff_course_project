package edu.java.bot.scrapperClient.exceptions.status;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class TooManyRequestsException extends StatusException {
    @Getter
    private final HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
}
