package edu.java.service.jpa;

import edu.java.exceptions.ChatAlreadyRegisteredException;
import edu.java.exceptions.ChatNotExistException;
import edu.java.model.entity.TgChat;
import edu.java.scrapper.JpaIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@Rollback
@Transactional
class JpaTgChatServiceTest extends JpaIntegrationTest {

    @Test
    void registerChat_shouldReturnChatWithId() {
        TgChat res = jpaTgChatService.registerChat(1L, "MyChat");

        assertThat(res.getId()).isNotNull();
    }

    @Test
    void registerExistsChat_shouldThrowChatAlreadyRegisteredException() {
        long chatId = 1L;
        jpaTgChatService.registerChat(chatId, "MyChat");
        Throwable res = catchThrowable(() -> jpaTgChatService.registerChat(chatId, "MyChat"));

        assertThat(res).isInstanceOf(ChatAlreadyRegisteredException.class);
    }

    @Test
    void unregisterChat_shouldReturnChatWithSameChatId() {
        long chatId = 11L;
        TgChat savedChat = jpaTgChatService.registerChat(chatId, "MyChat");

        TgChat removedChat = jpaTgChatService.unregisterChat(chatId);

        assertThat(removedChat).isEqualTo(savedChat);
    }

    @Test
    void unregisterNotExistsChat_shouldThrowChatNotExistsException() {
        long chatId = 11L;
        Throwable res = catchThrowable(() -> jpaTgChatService.unregisterChat(chatId));

        assertThat(res).isInstanceOf(ChatNotExistException.class);
    }
}
