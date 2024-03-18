package edu.java.domain.jooqRepository;

import edu.java.model.Link;
import edu.java.model.TgChat;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class JooqTgChatRepositoryTest {

    @Autowired
    private JooqTgChatRepository chatRepository;
    @Autowired
    private JooqLinkRepository linkRepository;

    @Test
    @Rollback
    @Transactional
    void insertTgChat() {
        TgChat chat = new TgChat(123L, "aa");

        TgChat res = chatRepository.insertTgChat(chat).get();
        chat.setId(res.getId());    // for clean equals

        assertThat(res).isEqualTo(chat);
    }

    @Test
    @Rollback
    @Transactional
    void insertTgChatShouldCreatesWithDifferentIds() {
        TgChat chat = new TgChat(123L, "aa");

        TgChat res1 = chatRepository.insertTgChat(chat).get();
        chat.setChatId(124L);
        TgChat res2 = chatRepository.insertTgChat(chat).get();

        assertThat(res1.getId()).isNotEqualTo(res2.getId());
    }

    @Test
    @Rollback
    @Transactional
    void insertTgChatWithIdShouldIgnoreIt() {
        TgChat chat = new TgChat(111L, 11L, "aa");
        TgChat res1 = chatRepository.insertTgChat(chat).get();
        chat.setChatId(112L);
        TgChat res2 = chatRepository.insertTgChat(chat).get();

        // if ids not equal them not equal 111
        assertThat(res1.getId()).isNotEqualTo(res2.getId());
    }

    @Test
    @Rollback
    @Transactional
    void updateTgChat() {
        long oldChatId = 123L;
        long newChatId = 125L;
        TgChat oldChat = new TgChat(oldChatId, "aa");
        TgChat newChat = new TgChat(newChatId, "aa");
        long id = chatRepository.insertTgChat(oldChat).get().getId();

        chatRepository.updateTgChat(id, newChat);
        TgChat res = chatRepository.findTgChatById(id).get();

        assertThat(res.getChatId()).isEqualTo(newChatId);
    }

    @Test
    @Rollback
    @Transactional
    void updateTgChatWithWrongIdShouldReturnEmptyOptional() {
        long randomId = 11L;
        TgChat newChat = new TgChat(123L, "aa");

        Optional<TgChat> res = chatRepository.updateTgChat(randomId, newChat);

        assertThat(res.isEmpty()).isTrue();
    }

    @Test
    @Rollback
    @Transactional
    void removeTgChatByIdShouldReturnWhatItDeleted() {
        TgChat chat = new TgChat(123L, "aa");
        long id = chatRepository.insertTgChat(chat).get().getId();

        Optional<TgChat> removeRes = chatRepository.removeTgChatById(id);
        Optional<TgChat> findAfterRemoveRes = chatRepository.findTgChatById(id);
        chat.setId(removeRes.get().getId());    // for clean equals

        assertThat(removeRes.get()).isEqualTo(chat);
        assertThat(findAfterRemoveRes.isEmpty()).isTrue();
    }

    @Test
    @Rollback
    @Transactional
    void removeTgChatWithWrongIdShouldDoSimilarWithNullRemoveRes() {
        long id = 15L;

        Optional<TgChat> removeRes = chatRepository.removeTgChatById(id);
        Optional<TgChat> findAfterRemoveRes = chatRepository.findTgChatById(id);

        assertThat(removeRes.isEmpty()).isTrue();
        assertThat(findAfterRemoveRes.isEmpty()).isTrue();
    }

    @Test
    @Rollback
    @Transactional
    void findTgChatById() {
        TgChat chat = new TgChat(123L, "aa");
        long id = chatRepository.insertTgChat(chat).get().getId();

        TgChat res = chatRepository.findTgChatById(id).get();
        chat.setId(res.getId());    // for clean equals

        assertThat(res).isEqualTo(chat);
    }

    @Test
    @Rollback
    @Transactional
    void findTgChatByWrongIdShouldReturnEmptyOptional() {
        long id = 123L;

        Optional<TgChat> res = chatRepository.findTgChatById(id);

        assertThat(res.isEmpty()).isTrue();
    }

    @Test
    @Rollback
    @Transactional
    void findTgChatByChatId() {
        long chatId = 123L;
        TgChat chat = new TgChat(chatId, "chatName");
        chatRepository.insertTgChat(chat);

        TgChat res = chatRepository.findTgChatByChatId(chatId).get();

        assertThat(res.getChatId()).isEqualTo(chat.getChatId());
    }

    @Test
    @Rollback
    @Transactional
    void findLinkByUncreatedURL() {
        long chatId = 123L;

        Optional<TgChat> res = chatRepository.findTgChatByChatId(chatId);

        assertThat(res.isEmpty()).isTrue();
    }

    @Test
    @Rollback
    @Transactional
    void findAllTgChats() {
        int insertCount = 5;
        TgChat chat = new TgChat(123L, "aa");

        Stream.iterate(1L, it -> it + 1)
            .limit(insertCount)
            .forEach(it -> chatRepository.insertTgChat(createTgChatWithChatId(it)));

        List<TgChat> res = chatRepository.findAllTgChats();

        assertThat(res.size()).isEqualTo(insertCount);
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

        chatWithLink1 = chatRepository.insertTgChat(chatWithLink1).get();
        chatWithLink2 = chatRepository.insertTgChat(chatWithLink2).get();
        chatRepository.insertTgChat(chatWithoutLink).get();

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
        Long linkId = linkRepository.insertLink(link).get().getId();

        Set<TgChat> resChats = new HashSet<>(chatRepository.findTgChatsByLinkId(linkId));

        assertThat(resChats).isEqualTo(chatsWithLink);
    }

}
