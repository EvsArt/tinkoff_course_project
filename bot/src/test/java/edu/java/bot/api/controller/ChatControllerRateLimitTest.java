package edu.java.bot.api.controller;

import edu.java.bot.api.dto.LinkUpdateRequest;
import edu.java.bot.bot.TgBot;
import io.github.bucket4j.Bucket;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UpdatesController.class)
public class ChatControllerRateLimitTest {

    @Autowired MockMvc mockMvc;
    @MockBean TgBot chatService;
    @MockBean(name = "updatesRateLimitBucket") Bucket bucket;

    @Test
    void postRequest_shouldReturnTooManyRequests() throws Exception {
        Mockito.when(bucket.tryConsume(Mockito.anyLong())).thenReturn(false);
        mockMvc.perform(post("/updates")
                .content(new ObjectMapper().writeValueAsBytes(
                    LinkUpdateRequest.builder()
                        .url("url")
                        .tgChatIds(List.of(1L))
                        .build())
                )
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    void postRequest_shouldReturnOk() throws Exception {
        Mockito.when(bucket.tryConsume(Mockito.anyLong())).thenReturn(true);
        mockMvc.perform(post("/updates")
                .content(new ObjectMapper().writeValueAsBytes(
                    LinkUpdateRequest.builder()
                        .url("url")
                        .tgChatIds(List.of(1L))
                        .build())
                )
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

}
