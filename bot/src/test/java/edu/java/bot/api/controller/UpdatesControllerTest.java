package edu.java.bot.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.api.dto.LinkUpdateRequest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UpdatesController.class)
class UpdatesControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void postUpdateShouldReturnOk() throws Exception {

        LinkUpdateRequest requestBody = LinkUpdateRequest.builder()
            .id(123L)
            .url("https://example.com")
            .description("My Link")
            .tgChatIds(List.of(1L, 2L, 3L))
            .build();

        mockMvc.perform(post("/updates")
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void postUpdateWithWrongArgumentsShouldReturnBadRequest() throws Exception {

        LinkUpdateRequest requestBody = LinkUpdateRequest.builder()
            .id(123L)
            .url("https://example.com")
            .description("My Link")
            .tgChatIds(List.of())
            .build();

        mockMvc.perform(post("/updates")
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.exceptionName").value(MethodArgumentNotValidException.class.getName()));
    }

}
