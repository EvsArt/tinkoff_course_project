package edu.java.exceptions.status;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class MovedPermanentlyException extends StatusException {

    @Getter
    private final HttpStatus status = HttpStatus.MOVED_PERMANENTLY;

}
