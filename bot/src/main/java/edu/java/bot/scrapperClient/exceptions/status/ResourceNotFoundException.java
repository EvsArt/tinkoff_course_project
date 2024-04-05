package edu.java.bot.scrapperClient.exceptions.status;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends StatusException {

    @Getter
    private final HttpStatus status = HttpStatus.NOT_FOUND;

}
