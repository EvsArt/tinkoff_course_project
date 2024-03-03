package edu.java.bot.api.controller;

import edu.java.bot.api.dto.ApiErrorResponse;
import edu.java.bot.api.exceptions.InvalidUpdateException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackageClasses = UpdatesController.class)
public class ControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidUpdateException.class)
    protected ResponseEntity<Object> handleInvalidUpdate(
        RuntimeException ex, WebRequest request
    ) {
        HttpStatusCode status = HttpStatus.BAD_REQUEST;

        ApiErrorResponse response =
            ApiErrorResponse.builder()
                .description("Invalid update")
                .code(String.valueOf(status.value()))
                .exceptionName(ex.getClass().getName())
                .exceptionMessage(ex.getMessage())
                .stacktrace(
                    Arrays.stream(ex.getStackTrace())
                        .map(StackTraceElement::toString)
                        .toList()
                )
                .build();

        MultiValueMap<String, String> headers = new MultiValueMapAdapter<>(Map.of(
            HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON.toString())
        ));
        return createResponseEntity(
            response,
            HttpHeaders.readOnlyHttpHeaders(headers),
            status,
            request
        );
    }

}
