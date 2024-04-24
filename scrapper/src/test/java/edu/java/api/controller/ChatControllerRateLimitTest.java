package edu.java.api.controller;

import edu.java.service.TgChatService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@TestPropertySource("classpath:/test.env")
public class ChatControllerRateLimitTest {

    @Autowired MockMvc mockMvc;
    @MockBean TgChatService chatService;
    @MockBean(name = "tgChatRateLimitBucket") Bucket bucket;

    @Test
    void postRequest_shouldReturnTooManyRequests() throws Exception {
        Mockito.when(bucket.tryConsume(Mockito.anyLong())).thenReturn(false);
        mockMvc.perform(post("/tg-chat/" + 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    void deleteRequest_shouldReturnTooManyRequests() throws Exception {
        Mockito.when(bucket.tryConsume(Mockito.anyLong())).thenReturn(false);
        mockMvc.perform(delete("/tg-chat/" + 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    void postRequest_shouldReturnOk() throws Exception {
        Mockito.when(bucket.tryConsume(Mockito.anyLong())).thenReturn(true);
        mockMvc.perform(post("/tg-chat/" + 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void deleteRequest_shouldReturnOk() throws Exception {
        Mockito.when(bucket.tryConsume(Mockito.anyLong())).thenReturn(true);
        mockMvc.perform(delete("/tg-chat/" + 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

}
