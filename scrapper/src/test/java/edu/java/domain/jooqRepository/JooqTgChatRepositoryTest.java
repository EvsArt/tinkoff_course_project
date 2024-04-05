package edu.java.domain.jooqRepository;

import edu.java.model.entity.Link;
import edu.java.model.entity.TgChat;
import edu.java.scrapper.JooqIntegrationTest;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Rollback
@Transactional
class JooqTgChatRepositoryTest extends JooqIntegrationTest {

    @Test
    void insertTgChat() {
        TgChat chat = new TgChat(123L, "aa");

        TgChat res = jooqTgChatRepository.insertTgChat(chat).get();
        chat.setId(res.getId());    // for clean equals

        assertThat(res).isEqualTo(chat);
    }

    @Test
    void insertTgChatShouldCreatesWithDifferentIds() {
        TgChat chat = new TgChat(123L, "aa");

        TgChat res1 = jooqTgChatRepository.insertTgChat(chat).get();
        chat.setChatId(124L);
        TgChat res2 = jooqTgChatRepository.insertTgChat(chat).get();

        assertThat(res1.getId()).isNotEqualTo(res2.getId());
    }

    @Test
    void insertTgChatWithIdShouldIgnoreIt() {
        TgChat chat = new TgChat(111L, 11L, "aa");
        TgChat res1 = jooqTgChatRepository.insertTgChat(chat).get();
        chat.setChatId(112L);
        TgChat res2 = jooqTgChatRepository.insertTgChat(chat).get();

        // if ids not equal them not equal 111
        assertThat(res1.getId()).isNotEqualTo(res2.getId());
    }

    @Test
    void updateTgChat() {
        long oldChatId = 123L;
        long newChatId = 125L;
        TgChat oldChat = new TgChat(oldChatId, "aa");
        TgChat newChat = new TgChat(newChatId, "aa");
        long id = jooqTgChatRepository.insertTgChat(oldChat).get().getId();

        TgChat res = jooqTgChatRepository.updateTgChat(id, newChat).get();

        assertThat(res.getChatId()).isEqualTo(newChatId);
    }

    @Test
    void updateTgChatWithWrongIdShouldReturnEmptyOptional() {
        long randomId = 11L;
        TgChat newChat = new TgChat(123L, "aa");

        Optional<TgChat> res = jooqTgChatRepository.updateTgChat(randomId, newChat);

        assertThat(res).isEmpty();
    }

    @Test
    void removeTgChatByIdShouldReturnWhatItDeleted() {
        TgChat chat = new TgChat(123L, "aa");
        long id = jooqTgChatRepository.insertTgChat(chat).get().getId();

        Optional<TgChat> removeRes = jooqTgChatRepository.removeTgChatById(id);
        Optional<TgChat> findAfterRemoveRes = jooqTgChatRepository.findTgChatById(id);
        chat.setId(removeRes.get().getId());    // for clean equals

        assertThat(removeRes.get()).isEqualTo(chat);
        assertThat(findAfterRemoveRes).isEmpty();
    }

    @Test
    void removeTgChatWithWrongIdShouldDoSimilarWithNullRemoveRes() {
        long id = 15L;

        Optional<TgChat> removeRes = jooqTgChatRepository.removeTgChatById(id);
        Optional<TgChat> findAfterRemoveRes = jooqTgChatRepository.findTgChatById(id);

        assertThat(removeRes).isEmpty();
        assertThat(findAfterRemoveRes).isEmpty();
    }

    @Test
    void findTgChatById() {
        TgChat chat = new TgChat(123L, "aa");
        long id = jooqTgChatRepository.insertTgChat(chat).get().getId();

        TgChat res = jooqTgChatRepository.findTgChatById(id).get();
        chat.setId(res.getId());    // for clean equals

        assertThat(res).isEqualTo(chat);
    }

    @Test
    void findTgChatByWrongIdShouldReturnEmptyOptional() {
        long id = 123L;

        Optional<TgChat> res = jooqTgChatRepository.findTgChatById(id);

        assertThat(res).isEmpty();
    }

    @Test
    void findTgChatByChatId() {
        long chatId = 123L;
        TgChat chat = new TgChat(chatId, "chatName");
        jooqTgChatRepository.insertTgChat(chat);

        TgChat res = jooqTgChatRepository.findTgChatByChatId(chatId).get();

        assertThat(res.getChatId()).isEqualTo(chat.getChatId());
    }

    @Test
    void findUncreatedLink() {
        long chatId = 123L;

        Optional<TgChat> res = jooqTgChatRepository.findTgChatByChatId(chatId);

        assertThat(res).isEmpty();
    }

    @Test
    void findAllTgChats() {
        int insertCount = 5;

        Stream.iterate(1L, it -> it + 1)
            .limit(insertCount)
            .forEach(it -> jooqTgChatRepository.insertTgChat(createTgChatWithChatId(it)));

        List<TgChat> res = jooqTgChatRepository.findAllTgChats();

        AssertionsForInterfaceTypes.assertThat(res).hasSize(insertCount);
    }

    private TgChat createTgChatWithChatId(Long chatId) {
        return new TgChat(chatId, "name");
    }

    @Test
    void findTgChatsByLinkId() {

        TgChat chatWithLink1 = new TgChat(1L, "myChat");
        TgChat chatWithLink2 = new TgChat(2L, "myChat2");
        TgChat chatWithoutLink = new TgChat(3L, "myChat3");

        chatWithLink1 = jooqTgChatRepository.insertTgChat(chatWithLink1).get();
        chatWithLink2 = jooqTgChatRepository.insertTgChat(chatWithLink2).get();
        jooqTgChatRepository.insertTgChat(chatWithoutLink).get();

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
        Long linkId = jooqLinkRepository.insertLink(link).get().getId();

        Set<TgChat> resChats = new HashSet<>(jooqTgChatRepository.findTgChatsByLinkId(linkId));

        assertThat(resChats).isEqualTo(chatsWithLink);
    }

}
