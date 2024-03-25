package edu.java.service.jooq;

import edu.java.exceptions.ChatAlreadyRegisteredException;
import edu.java.exceptions.ChatNotExistException;
import edu.java.model.entity.TgChat;
import edu.java.scrapper.JooqIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@Rollback
@Transactional
class JooqTgChatServiceTest extends JooqIntegrationTest {

    @Test
    void registerChat_shouldReturnChatWithId() {
        TgChat res = jooqTgChatService.registerChat(1L, "MyChat");

        assertThat(res.getId()).isNotNull();
    }

    @Test
    void registerExistsChat_shouldThrowChatAlreadyRegisteredException() {
        jooqTgChatService.registerChat(1L, "MyChat");
        Throwable res = catchThrowable(() -> jooqTgChatService.registerChat(1L, "MyChat"));

        assertThat(res).isInstanceOf(ChatAlreadyRegisteredException.class);
    }

    @Test
    void unregisterChat_shouldReturnChatWithSameChatId() {
        long chatId = 11L;
        TgChat savedChat = jooqTgChatService.registerChat(chatId, "MyChat");

        TgChat removedChat = jooqTgChatService.unregisterChat(chatId);

        assertThat(removedChat).isEqualTo(savedChat);
    }

    @Test
    void unregisterNotExistsChat_shouldThrowChatNotExistsException() {
        long chatId = 11L;
        Throwable res = catchThrowable(() -> jooqTgChatService.unregisterChat(chatId));

        assertThat(res).isInstanceOf(ChatNotExistException.class);
    }
}
