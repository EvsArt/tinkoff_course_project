package edu.java.api.controller;

import edu.java.api.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface IChatController {

    @Operation(summary = "Зарегистрировать чат", description = "", tags = {})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат зарегистрирован"),

        @ApiResponse(responseCode = "400",
                     description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))})
    ResponseEntity<Void> registerChat(@PathVariable Long id);

    @Operation(summary = "Удалить чат", description = "", tags={  })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат успешно удалён"),

        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))),

        @ApiResponse(responseCode = "404", description = "Чат не существует", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))) })
    ResponseEntity<Void> deleteChat(@PathVariable Long id);

}
