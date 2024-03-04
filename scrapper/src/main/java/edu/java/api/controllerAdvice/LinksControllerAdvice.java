package edu.java.api.controllerAdvice;

import edu.java.api.controller.LinksController;
import edu.java.api.dto.ApiErrorResponse;
import edu.java.api.exceptions.ChatNotExistException;
import edu.java.api.exceptions.LinkNotExistsException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackageClasses = LinksController.class)
public class LinksControllerAdvice extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity<Object> handleTypeMismatch(
        TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        ApiErrorResponse response =
            ApiErrorResponse.builder()
                .description("Wrong type of input value")
                .code(String.valueOf(status.value()))
                .exceptionName(ex.getClass().getName())
                .exceptionMessage(ex.getMessage())
                .stacktrace(
                    Arrays.stream(ex.getStackTrace())
                        .map(StackTraceElement::toString)
                        .toList()
                )
                .build();

        MultiValueMap<String, String> newHeaders = new MultiValueMapAdapter<>(Map.of(
            HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON.toString())
        ));

        headers.addAll(newHeaders);

        return createResponseEntity(
            response,
            HttpHeaders.readOnlyHttpHeaders(headers),
            status,
            request
        );
    }

    @ExceptionHandler(LinkNotExistsException.class)
    public ResponseEntity<Object> handleLinkNotExists(
        RuntimeException ex, WebRequest request
    ) {
        HttpStatusCode status = HttpStatus.NOT_FOUND;

        ApiErrorResponse response =
            ApiErrorResponse.builder()
                .description("Link not exists")
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

    @ExceptionHandler(ChatNotExistException.class)
    public ResponseEntity<Object> handleChatNotExists(
        RuntimeException ex, WebRequest request
    ) {
        HttpStatusCode status = HttpStatus.BAD_REQUEST;

        ApiErrorResponse response =
            ApiErrorResponse.builder()
                .description("Chat not exists")
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

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        System.out.println("AAAAAAAAA");
        ApiErrorResponse response =
            ApiErrorResponse.builder()
                .description("Invalid request format")
                .code(String.valueOf(status.value()))
                .exceptionName(ex.getClass().getName())
                .exceptionMessage(ex.getMessage())
                .stacktrace(
                    Arrays.stream(ex.getStackTrace())
                        .map(StackTraceElement::toString)
                        .toList()
                )
                .build();

        MultiValueMap<String, String> newHeaders = new MultiValueMapAdapter<>(Map.of(
            HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON.toString())
        ));

        return createResponseEntity(
            response,
            HttpHeaders.readOnlyHttpHeaders(newHeaders),
            status,
            request
        );
    }

}
