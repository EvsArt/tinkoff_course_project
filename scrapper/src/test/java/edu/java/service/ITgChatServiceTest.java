package edu.java.service;

import edu.java.exceptions.ChatAlreadyRegisteredException;
import edu.java.exceptions.ChatNotExistException;
import edu.java.model.TgChat;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@SpringBootTest
class ITgChatServiceTest extends IntegrationTest {

    @Autowired
    private ITgChatService chatService;

    @Test
    @Rollback
    @Transactional
    void registerChat_shouldReturnChatWithId() {
        TgChat res = chatService.registerChat(1L, "MyChat");

        assertThat(res.getId()).isNotNull();
    }

    @Test
    @Rollback
    @Transactional
    void registerExistsChat_shouldThrowChatAlreadyRegisteredException() {
        chatService.registerChat(1L, "MyChat");
        Throwable res = catchThrowable(() -> chatService.registerChat(1L, "MyChat"));

        assertThat(res).isInstanceOf(ChatAlreadyRegisteredException.class);
    }

    @Test
    @Rollback
    @Transactional
    void unregisterChat_shouldReturnChatWithSameChatId() {
        long chatId = 11L;
        TgChat savedChat = chatService.registerChat(chatId, "MyChat");

        TgChat removedChat = chatService.unregisterChat(chatId);

        assertThat(removedChat).isEqualTo(savedChat);
    }

    @Test
    @Rollback
    @Transactional
    void unregisterNotExistsChat_shouldThrowChatNotExistsException() {
        long chatId = 11L;
        Throwable res = catchThrowable(() -> chatService.unregisterChat(chatId));

        assertThat(res).isInstanceOf(ChatNotExistException.class);
    }
}