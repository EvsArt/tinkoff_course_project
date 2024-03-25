package edu.java.domain.jpaRepository;

import edu.java.model.entity.Link;
import edu.java.model.entity.TgChat;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import edu.java.scrapper.JpaIntegrationTest;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Rollback
@Transactional
class JpaLinkRepositoryTest extends JpaIntegrationTest {

    @Autowired
    private JpaLinkRepository jpaLinkRepository;
    @Autowired
    private JpaTgChatRepository jpaTgChatRepository;

    @Test
    void insertLink() {
        Link link = new Link(
            URI.create("https://github.com/me/myRep"),
            "MyRep",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );
        TgChat chat1 = new TgChat(1L, "a");
        TgChat chat2 = new TgChat(2L, "a");
        chat1 = jpaTgChatRepository.save(chat1);
        chat2 = jpaTgChatRepository.save(chat2);
        link.setTgChats(Set.of(chat1, chat2));

        Link res = jpaLinkRepository.save(link);

        assertThat(res.getName()).isEqualTo(link.getName());
        assertThat(res.getUrl()).isEqualTo(link.getUrl());
        assertThat(res.getCreatedAt()).isEqualTo(link.getCreatedAt());
        assertThat(res.getTgChats()).isEqualTo(link.getTgChats());
    }

    @Test
    void updateLink() {
        String oldName = "name1";
        String newName = "name2";
        Link oldLink = new Link(
            URI.create("https://github.com/me/myRep"),
            oldName,
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );
        Link newLink = new Link(
            URI.create("https://github.com/me/myRep"),
            newName,
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );
        TgChat chat1 = new TgChat(1L, "a");
        TgChat chat2 = new TgChat(2L, "a");
        TgChat chat3 = new TgChat(3L, "a");
        chat1 = jpaTgChatRepository.save(chat1);
        chat2 = jpaTgChatRepository.save(chat2);
        chat3 = jpaTgChatRepository.save(chat3);
        Set<TgChat> chats12 = new HashSet<>();
        chats12.addAll(List.of(chat1, chat2));
        Set<TgChat> chats32 = new HashSet<>();
        chats32.addAll(List.of(chat3, chat2));

        long id = jpaLinkRepository.save(oldLink).getId();

        newLink.setId(id);
        jpaLinkRepository.save(newLink);

        Link res = jpaLinkRepository.findById(id).get();

        assertThat(res.getName()).isEqualTo(newName);

        AssertionsForInterfaceTypes.assertThat(res.getTgChats()).hasSize(newLink.getTgChats().size());
        AssertionsForInterfaceTypes.assertThat(res.getTgChats()).containsAll(newLink.getTgChats());
        AssertionsForInterfaceTypes.assertThat(newLink.getTgChats()).containsAll(res.getTgChats());
    }

    @Test
    void findLinkById() {
        Link link = new Link(
            URI.create("https://github.com/me/myRep"),
            "MyRep",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );
        long id = jpaLinkRepository.save(link).getId();

        Link res = jpaLinkRepository.findById(id).get();

        assertThat(res.getUrl()).isEqualTo(link.getUrl());
    }

    @Test
    void findLinkByWrongIdShouldReturnEmptyOptional() {
        long id = 123L;

        Optional<Link> res = jpaLinkRepository.findById(id);

        assertThat(res).isEmpty();
    }

    @Test
    void findLinkByURL() {
        URI url = URI.create("https://github.com/me/myRep");
        Link link = new Link(
            url,
            "MyRep",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );
        jpaLinkRepository.save(link);

        Link res = jpaLinkRepository.findByUrl(url).get();

        assertThat(res.getUrl()).isEqualTo(link.getUrl());
    }

    @Test
    void findLinkByUncreatedURL() {
        URI url = URI.create("https://github.com/me/myRep");

        Optional<Link> res = jpaLinkRepository.findByUrl(url);

        assertThat(res).isEmpty();
    }

    @Test
    void findAllTgChats() {
        int insertCount = 5;
        Link link = new Link(
            URI.create("https://github.com/me/myRep"),
            "MyRep",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );

        Stream.iterate(1, it -> it + 1).limit(insertCount)
            .forEach(it -> jpaLinkRepository.save(createLinkWithUniqueUrlWithNum(it)));

        List<Link> res = jpaLinkRepository.findAll();

        AssertionsForInterfaceTypes.assertThat(res).hasSize(insertCount);
    }

    @Test
    void findLinksByTgChatId() {
        Link link1 = new Link(
            URI.create("https://github.com/me/myRep1"),
            "name",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );
        Link link2 = new Link(
            URI.create("https://github.com/me/myRep2"),
            "name",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );
        TgChat chat1 = new TgChat(12L, "a");
        TgChat chat2 = new TgChat(22L, "a");
        chat1 = jpaTgChatRepository.save(chat1);
        chat2 = jpaTgChatRepository.save(chat2);
        link1.setTgChats(Set.of(chat1, chat2));
        link2.setTgChats(Set.of(chat2));

        link1 = jpaLinkRepository.save(link1);
        jpaLinkRepository.save(link2);

        List<Link> res = jpaLinkRepository.findByTgChatsContains(chat1);

        AssertionsForInterfaceTypes.assertThat(res).hasSize(1);
        assertThat(res.getFirst()).isEqualTo(link1);
    }

    @Test
    void findAllWhereLastCheckTimeBefore() {
        OffsetDateTime needLastCheckTime = OffsetDateTime.parse("2024-03-15T11:17:31+03:00");
        Link checkedLink = new Link(
            URI.create("https://github.com/me/myRep1"),
            "name1",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00"),
            needLastCheckTime.plusDays(1)
        );
        Link uncheckedLink = new Link(
            URI.create("https://github.com/me/myRep2"),
            "name2",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00"),
            needLastCheckTime.minusDays(1)
        );

        jpaLinkRepository.save(checkedLink);
        uncheckedLink = jpaLinkRepository.save(uncheckedLink);

        List<Link> res = jpaLinkRepository.findByLastCheckTimeIsBefore(needLastCheckTime);

        AssertionsForInterfaceTypes.assertThat(res).hasSize(1);
        assertThat(res.getFirst()).isEqualTo(uncheckedLink);
    }

    private Link createLinkWithUniqueUrlWithNum(int num) {
        return new Link(
            URI.create("https://github.com/me/myRep" + num),
            "MyRep",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );
    }
}
