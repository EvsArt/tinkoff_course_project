package edu.java.service;

import edu.java.model.TgChat;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
    void registerExistsChat_shouldReturnChatWithId() {
        TgChat res1 = chatService.registerChat(1L, "MyChat");
        TgChat res2 = chatService.registerChat(1L, "MyChat");

        assertThat(res1).isEqualTo(res2);
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
    void unregisterNotExistsChat_shouldReturnChatWithoutId() {
        long chatId = 11L;
        TgChat removedChat = chatService.unregisterChat(chatId);

        assertThat(removedChat.getId()).isNull();
    }
}
