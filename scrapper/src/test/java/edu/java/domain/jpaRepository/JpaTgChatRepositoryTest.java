package edu.java.domain.jpaRepository;

import edu.java.model.entity.Link;
import edu.java.model.entity.TgChat;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JpaTgChatRepositoryTest extends IntegrationTest {

    @Autowired
    private JpaTgChatRepository chatRepository;
    @Autowired
    private JpaLinkRepository linkRepository;

    @Test
    @Rollback
    @Transactional
    void insertTgChat() {
        TgChat chat = new TgChat(123L, "aa");

        TgChat res = chatRepository.save(chat);
        chat.setId(res.getId());    // for clean equals

        assertThat(res).isEqualTo(chat);
    }

    @Test
    @Rollback
    @Transactional
    void updateTgChat() {
        long oldChatId = 123L;
        long newChatId = 124L;
        TgChat oldChat = new TgChat(oldChatId, "aa");
        TgChat newChat = new TgChat(newChatId, "aa");
        long id = chatRepository.save(oldChat).getId();
        newChat.setId(id);
        chatRepository.save(newChat);

        TgChat res = chatRepository.findById(id).get();

        assertThat(res).isEqualTo(newChat);
    }

    @Test
    @Rollback
    @Transactional
    void findTgChatById() {
        TgChat chat = new TgChat(123L, "aa");
        long id = chatRepository.save(chat).getId();

        TgChat res = chatRepository.findById(id).get();
        chat.setId(res.getId());    // for clean equals

        assertThat(res).isEqualTo(chat);
    }

    @Test
    @Rollback
    @Transactional
    void findTgChatByWrongIdShouldReturnEmptyOptional() {
        long id = 123L;

        Optional<TgChat> res = chatRepository.findById(id);

        assertThat(res).isEmpty();
    }

    @Test
    @Rollback
    @Transactional
    void findTgChatByChatId() {
        long chatId = 123L;
        TgChat chat = new TgChat(chatId, "chatName");
        chatRepository.save(chat);

        TgChat res = chatRepository.findTgChatByChatId(chatId).get();

        assertThat(res.getChatId()).isEqualTo(chat.getChatId());
    }

    @Test
    @Rollback
    @Transactional
    void findLinkByUncreatedURL() {
        long chatId = 123L;

        Optional<TgChat> res = chatRepository.findTgChatByChatId(chatId);

        assertThat(res).isEmpty();
    }

    @Test
    @Rollback
    @Transactional
    void findAllTgChats() {
        int insertCount = 5;
        TgChat chat = new TgChat(123L, "aa");

        Stream.iterate(1L, it -> it + 1)
            .limit(insertCount)
            .forEach(it -> chatRepository.save(createTgChatWithChatId(it)));

        List<TgChat> res = chatRepository.findAll();

        AssertionsForInterfaceTypes.assertThat(res).hasSize(insertCount);
    }

    private TgChat createTgChatWithChatId(Long chatId) {
        return new TgChat(chatId, "name");
    }

    @Test
    @Transactional
    @Rollback
    void findTgChatsByLinkId() {

        TgChat chatWithLink1 = new TgChat(1L, "myChat");
        TgChat chatWithLink2 = new TgChat(2L, "myChat2");
        TgChat chatWithoutLink = new TgChat(3L, "myChat3");

        chatWithLink1 = chatRepository.save(chatWithLink1);
        chatWithLink2 = chatRepository.save(chatWithLink2);
        chatRepository.save(chatWithoutLink);

        Set<TgChat> chatsWithLink = Set.of(
            chatWithLink1, chatWithLink2
        );

        Link link = new Link(
            URI.create("https://github.com/me/myRep"),
            "MyRep",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );
        link.setTgChats(chatsWithLink);
        Long linkId = linkRepository.save(link).getId();

        Set<TgChat> resChats = new HashSet<>(linkRepository.findById(linkId).get().getTgChats());

        assertThat(resChats).isEqualTo(chatsWithLink);
    }

}
