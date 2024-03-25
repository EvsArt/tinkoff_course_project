package edu.java.service.jdbc;

import edu.java.exceptions.ChatAlreadyRegisteredException;
import edu.java.exceptions.ChatNotExistException;
import edu.java.model.entity.TgChat;
import edu.java.scrapper.JdbcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@Rollback
@Transactional
class JdbcTgChatServiceTest extends JdbcIntegrationTest {

    @Test
    void registerChat_shouldReturnChatWithId() {
        TgChat res = jdbcTgChatService.registerChat(1L, "MyChat");

        assertThat(res.getId()).isNotNull();
    }

    @Test
    void registerExistsChat_shouldThrowChatAlreadyRegisteredException() {
        jdbcTgChatService.registerChat(1L, "MyChat");
        Throwable res = catchThrowable(() -> jdbcTgChatService.registerChat(1L, "MyChat"));

        assertThat(res).isInstanceOf(ChatAlreadyRegisteredException.class);
    }

    @Test
    void unregisterChat_shouldReturnChatWithSameChatId() {
        long chatId = 11L;
        TgChat savedChat = jdbcTgChatService.registerChat(chatId, "MyChat");

        TgChat removedChat = jdbcTgChatService.unregisterChat(chatId);

        assertThat(removedChat).isEqualTo(savedChat);
    }

    @Test
    void unregisterNotExistsChat_shouldThrowChatNotExistsException() {
        long chatId = 11L;
        Throwable res = catchThrowable(() -> jdbcTgChatService.unregisterChat(chatId));

        assertThat(res).isInstanceOf(ChatNotExistException.class);
    }
}
