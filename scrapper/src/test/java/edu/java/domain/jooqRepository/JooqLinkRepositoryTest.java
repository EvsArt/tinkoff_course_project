package edu.java.domain.jooqRepository;

import edu.java.model.entity.Link;
import edu.java.model.entity.TgChat;
import edu.java.scrapper.JooqIntegrationTest;
import java.net.URI;
import java.time.OffsetDateTime;
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
class JooqLinkRepositoryTest extends JooqIntegrationTest {

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
        chat1 = jooqTgChatRepository.insertTgChat(chat1).get();
        chat2 = jooqTgChatRepository.insertTgChat(chat2).get();
        link.setTgChats(Set.of(chat1, chat2));

        Link res = jooqLinkRepository.insertLink(link).get();

        assertThat(res.getName()).isEqualTo(link.getName());
        assertThat(res.getUrl()).isEqualTo(link.getUrl());
        assertThat(res.getCreatedAt()).isEqualTo(link.getCreatedAt());
        assertThat(res.getTgChats()).isEqualTo(link.getTgChats());
    }

    @Test
    void insertLinkShouldCreatesWithDifferentIds() {
        Link link = new Link(
            URI.create("https://github.com/me/myRep"),
            "MyRep",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );
        TgChat chat1 = new TgChat(1L, "a");
        TgChat chat2 = new TgChat(2L, "a");
        chat1 = jooqTgChatRepository.insertTgChat(chat1).get();
        chat2 = jooqTgChatRepository.insertTgChat(chat2).get();
        link.setTgChats(Set.of(chat1, chat2));

        Link res1 = jooqLinkRepository.insertLink(link).get();
        link.setUrl(URI.create("https://github.com/me/myRep2"));    // bc url is unique in table
        Link res2 = jooqLinkRepository.insertLink(link).get();

        assertThat(res1.getId()).isNotEqualTo(res2.getId());
    }

    @Test
    void insertLinkWithIdShouldIgnoreIt() {
        Link link = new Link(
            URI.create("https://github.com/me/myRep"),
            "MyRep",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );

        Link res1 = jooqLinkRepository.insertLink(link).get();
        link.setUrl(URI.create("https://github.com/me/myRep2"));    // bc url is unique in table
        Link res2 = jooqLinkRepository.insertLink(link).get();

        // if ids not equal them not equal 111
        assertThat(res1.getId()).isNotEqualTo(res2.getId());
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
        chat1 = jooqTgChatRepository.insertTgChat(chat1).get();
        chat2 = jooqTgChatRepository.insertTgChat(chat2).get();
        chat3 = jooqTgChatRepository.insertTgChat(chat3).get();
        oldLink.setTgChats(Set.of(chat1, chat2));
        newLink.setTgChats(Set.of(chat3, chat2));

        long id = jooqLinkRepository.insertLink(oldLink).get().getId();

        jooqLinkRepository.updateLink(id, newLink);
        Link res = jooqLinkRepository.findLinkById(id).get();

        assertThat(res.getName()).isEqualTo(newName);

        AssertionsForInterfaceTypes.assertThat(res.getTgChats()).hasSize(newLink.getTgChats().size());
        AssertionsForInterfaceTypes.assertThat(res.getTgChats()).containsAll(newLink.getTgChats());
        AssertionsForInterfaceTypes.assertThat(newLink.getTgChats()).containsAll(res.getTgChats());
    }

    @Test
    void updateLinkWithWrongIdShouldReturnEmptyOptional() {
        long randomId = 11L;
        Link newLink = new Link(
            URI.create("https://github.com/me/myRep"),
            "MyRep",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );

        Optional<Link> res = jooqLinkRepository.updateLink(randomId, newLink);

        assertThat(res).isEmpty();
    }

    @Test
    void removeLinkByIdShouldReturnWhatItDeleted() {
        Link link = new Link(
            URI.create("https://github.com/me/myRep"),
            "MyRep",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );
        long id = jooqLinkRepository.insertLink(link).get().getId();

        Optional<Link> removeRes = jooqLinkRepository.removeLinkById(id);
        Optional<Link> findAfterRemoveRes = jooqLinkRepository.findLinkById(id);

        assertThat(removeRes.get().getUrl()).isEqualTo(removeRes.get().getUrl());
        assertThat(findAfterRemoveRes).isEmpty();
    }

    @Test
    void removeLinkWithWrongIdShouldDoSimilarWithNullRemoveRes() {
        long id = 15L;

        Optional<Link> removeRes = jooqLinkRepository.removeLinkById(id);
        Optional<Link> findAfterRemoveRes = jooqLinkRepository.removeLinkById(id);

        assertThat(removeRes).isEmpty();
        assertThat(findAfterRemoveRes).isEmpty();
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
        long id = jooqLinkRepository.insertLink(link).get().getId();

        Link res = jooqLinkRepository.findLinkById(id).get();

        assertThat(res.getUrl()).isEqualTo(link.getUrl());
    }

    @Test
    void findLinkByWrongIdShouldReturnEmptyOptional() {
        long id = 123L;

        Optional<Link> res = jooqLinkRepository.findLinkById(id);

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
        jooqLinkRepository.insertLink(link);

        Link res = jooqLinkRepository.findLinkByURL(url).get();

        assertThat(res.getUrl()).isEqualTo(link.getUrl());
    }

    @Test
    void findLinkByUncreatedURL() {
        URI url = URI.create("https://github.com/me/myRep");

        Optional<Link> res = jooqLinkRepository.findLinkByURL(url);

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
            .forEach(it -> jooqLinkRepository.insertLink(createLinkWithUniqueUrlWithNum(it)));

        List<Link> res = jooqLinkRepository.findAllLinks();

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
        TgChat chat1 = new TgChat(11L, "a");
        TgChat chat2 = new TgChat(21L, "a");
        chat1 = jooqTgChatRepository.insertTgChat(chat1).get();
        chat2 = jooqTgChatRepository.insertTgChat(chat2).get();
        link1.setTgChats(Set.of(chat1, chat2));
        link2.setTgChats(Set.of(chat2));

        link1 = jooqLinkRepository.insertLink(link1).get();
        jooqLinkRepository.insertLink(link2).get();

        List<Link> res = jooqLinkRepository.findLinksByTgChatId(chat1.getId());

        AssertionsForInterfaceTypes.assertThat(res).hasSize(1);
        assertThat(res.getFirst()).isEqualTo(link1);
    }

    @Test
    void removeLinksByTgChatId_linkWithoutChatsShouldToBeRemoved() {
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
        TgChat chat1 = new TgChat(1L, "a");
        TgChat chat2 = new TgChat(2L, "a");
        chat1 = jooqTgChatRepository.insertTgChat(chat1).get();
        chat2 = jooqTgChatRepository.insertTgChat(chat2).get();
        link1.setTgChats(Set.of(chat1, chat2));
        link2.setTgChats(Set.of(chat2));

        link1 = jooqLinkRepository.insertLink(link1).get();
        jooqLinkRepository.insertLink(link2).get();

        jooqLinkRepository.removeLinksByTgChatId(chat2.getId());
        List<Link> res = jooqLinkRepository.findAllLinks();
        // link2 should be removed bc only chat tracked it
        link1.setTgChats(Set.of(chat1));   // for clean equals (chat2 was removed)

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

        jooqLinkRepository.insertLink(checkedLink).get();
        uncheckedLink = jooqLinkRepository.insertLink(uncheckedLink).get();

        List<Link> res = jooqLinkRepository.findAllWhereLastCheckTimeBefore(needLastCheckTime);

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
