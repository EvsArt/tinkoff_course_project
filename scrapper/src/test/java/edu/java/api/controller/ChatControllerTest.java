package edu.java.api.controller;

import edu.java.exceptions.ChatAlreadyRegisteredException;
import edu.java.exceptions.ChatNotExistException;
import edu.java.model.entity.TgChat;
import edu.java.service.TgChatService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @MockBean TgChatService chatService;
    @Autowired MockMvc mockMvc;
    @MockBean(name = "tgChatRateLimitBucket") Bucket bucket;

    @BeforeEach
    void setBucket() {
        Mockito.when(bucket.tryConsume(Mockito.anyLong())).thenReturn(true);
    }

    @Test
    void postRegisterShouldReturnOk() throws Exception {
        Long chatId = 1L;
        String chatName = "myChat";
        Mockito.when(chatService.registerChat(chatId, chatName)).thenReturn(new TgChat(chatId, chatName));

        mockMvc.perform(post("/tg-chat/" + chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void postRegisterWithWrongArgumentsShouldReturnBadRequest() throws Exception {

        String fakeChatId = "1s";

        mockMvc.perform(post("/tg-chat/" + fakeChatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.exceptionName").value(MethodArgumentTypeMismatchException.class.getName()));
    }

    @Test
    void postRegisterExistedChatShouldReturnBadRequest() throws Exception {
        Long chatId = 1L;
        Mockito.when(chatService.registerChat(Mockito.eq(chatId), Mockito.any()))
            .thenThrow(ChatAlreadyRegisteredException.class);

        mockMvc.perform(post("/tg-chat/" + chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.exceptionName").value(ChatAlreadyRegisteredException.class.getName()));
    }

    @Test
    void deleteChatShouldReturnOk() throws Exception {
        long chatId = 1L;
        Mockito.when(chatService.unregisterChat(chatId)).thenReturn(new TgChat(chatId, ""));

        mockMvc.perform(delete("/tg-chat/" + chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void deleteChatWithWrongArgumentsShouldReturnBadRequest() throws Exception {

        String fakeChatId = "1s";

        mockMvc.perform(delete("/tg-chat/" + fakeChatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.exceptionName").value(MethodArgumentTypeMismatchException.class.getName()));
    }

    @Test
    void deleteNotExistedChatShouldReturnNotFound() throws Exception {
        Long chatId = 1L;
        Mockito.when(chatService.unregisterChat(chatId)).thenThrow(ChatNotExistException.class);

        mockMvc.perform(delete("/tg-chat/" + chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.exceptionName").value(ChatNotExistException.class.getName()));
    }

}
