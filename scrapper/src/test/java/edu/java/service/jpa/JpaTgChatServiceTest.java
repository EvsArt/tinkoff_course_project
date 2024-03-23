package edu.java.service.jpa;

import edu.java.exceptions.ChatAlreadyRegisteredException;
import edu.java.exceptions.ChatNotExistException;
import edu.java.model.entity.TgChat;
import edu.java.scrapper.IntegrationTest;
import edu.java.service.jooq.JooqTgChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@TestPropertySource(properties="app.database-access-type=jpa")
class JpaTgChatServiceTest extends IntegrationTest {

    @Autowired
    private JpaTgChatService chatService;

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
        long chatId = 1L;
        chatService.registerChat(chatId, "MyChat");
        Throwable res = catchThrowable(() -> chatService.registerChat(chatId, "MyChat"));

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
