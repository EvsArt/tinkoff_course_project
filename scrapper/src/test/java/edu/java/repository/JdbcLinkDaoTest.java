package edu.java.repository;

import edu.java.model.Link;
import edu.java.model.TgChat;
import edu.java.repository.jdbc.JdbcLinkDao;
import edu.java.repository.jdbc.JdbcTgChatRepository;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.time.OffsetDateTime;
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
class JdbcLinkDaoTest extends IntegrationTest {

    @Autowired
    private JdbcLinkDao linkRepository;
    @Autowired
    private JdbcTgChatRepository chatRepository;

    @Test
    @Rollback
    @Transactional
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
        chat1 = chatRepository.insertTgChat(chat1).get();
        chat2 = chatRepository.insertTgChat(chat2).get();
        link.setTgChats(Set.of(chat1, chat2));

        Link res = linkRepository.insertLink(link).get();

        assertThat(res.getName()).isEqualTo(link.getName());
        assertThat(res.getUrl()).isEqualTo(link.getUrl());
        assertThat(res.getCreatedAt()).isEqualTo(link.getCreatedAt());
        assertThat(res.getTgChats()).isEqualTo(link.getTgChats());
    }

    @Test
    @Rollback
    @Transactional
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
        chat1 = chatRepository.insertTgChat(chat1).get();
        chat2 = chatRepository.insertTgChat(chat2).get();
        link.setTgChats(Set.of(chat1, chat2));

        Link res1 = linkRepository.insertLink(link).get();
        link.setUrl(URI.create("https://github.com/me/myRep2"));    // bc url is unique in table
        Link res2 = linkRepository.insertLink(link).get();

        assertThat(res1.getId()).isNotEqualTo(res2.getId());
    }

    @Test
    @Rollback
    @Transactional
    void insertLinkWithIdShouldIgnoreIt() {
        Link link = new Link(
            URI.create("https://github.com/me/myRep"),
            "MyRep",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );

        Link res1 = linkRepository.insertLink(link).get();
        link.setUrl(URI.create("https://github.com/me/myRep2"));    // bc url is unique in table
        Link res2 = linkRepository.insertLink(link).get();

        // if ids not equal them not equal 111
        assertThat(res1.getId()).isNotEqualTo(res2.getId());
    }

    @Test
    @Rollback
    @Transactional
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
        chat1 = chatRepository.insertTgChat(chat1).get();
        chat2 = chatRepository.insertTgChat(chat2).get();
        chat3 = chatRepository.insertTgChat(chat3).get();
        oldLink.setTgChats(Set.of(chat1, chat2));
        newLink.setTgChats(Set.of(chat3, chat2));

        long id = linkRepository.insertLink(oldLink).get().getId();

        linkRepository.updateLink(id, newLink);
        Link res = linkRepository.findLinkById(id).get();

        assertThat(res.getName()).isEqualTo(newName);

        assertThat(res.getTgChats().size()).isEqualTo(newLink.getTgChats().size());
        assertThat(res.getTgChats().containsAll(newLink.getTgChats())).isTrue();
        assertThat(newLink.getTgChats().containsAll(res.getTgChats())).isTrue();
    }

    @Test
    @Rollback
    @Transactional
    void updateLinkWithWrongIdShouldReturnEmptyOptional() {
        long randomId = 11L;
        Link newLink = new Link(
            URI.create("https://github.com/me/myRep"),
            "MyRep",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );

        Optional<Link> res = linkRepository.updateLink(randomId, newLink);

        assertThat(res.isEmpty()).isTrue();
    }

    @Test
    @Rollback
    @Transactional
    void removeLinkByIdShouldReturnWhatItDeleted() {
        Link link = new Link(
            URI.create("https://github.com/me/myRep"),
            "MyRep",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );
        long id = linkRepository.insertLink(link).get().getId();

        Optional<Link> removeRes = linkRepository.removeLinkById(id);
        Optional<Link> findAfterRemoveRes = linkRepository.findLinkById(id);

        assertThat(removeRes.get().getUrl()).isEqualTo(removeRes.get().getUrl());
        assertThat(findAfterRemoveRes.isEmpty()).isTrue();
    }

    @Test
    @Rollback
    @Transactional
    void removeLinkWithWrongIdShouldDoSimilarWithNullRemoveRes() {
        long id = 15L;

        Optional<Link> removeRes = linkRepository.removeLinkById(id);
        Optional<Link> findAfterRemoveRes = linkRepository.removeLinkById(id);

        assertThat(removeRes.isEmpty()).isTrue();
        assertThat(findAfterRemoveRes.isEmpty()).isTrue();
    }

    @Test
    @Rollback
    @Transactional
    void findLinkById() {
        Link link = new Link(
            URI.create("https://github.com/me/myRep"),
            "MyRep",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );
        long id = linkRepository.insertLink(link).get().getId();

        Link res = linkRepository.findLinkById(id).get();

        assertThat(res.getUrl()).isEqualTo(link.getUrl());
    }

    @Test
    @Rollback
    @Transactional
    void findLinkByWrongIdShouldReturnEmptyOptional() {
        long id = 123L;

        Optional<Link> res = linkRepository.findLinkById(id);

        assertThat(res.isEmpty()).isTrue();
    }

    @Test
    @Rollback
    @Transactional
    void findLinkByURL() {
        URI url = URI.create("https://github.com/me/myRep");
        Link link = new Link(
            url,
            "MyRep",
            OffsetDateTime.parse("2024-03-15T11:15:30+03:00"),
            OffsetDateTime.parse("2024-03-15T11:17:31+03:00"),
            OffsetDateTime.parse("2024-03-15T11:19:32+03:00")
        );
        linkRepository.insertLink(link);

        Link res = linkRepository.findLinkByURL(url).get();

        assertThat(res.getUrl()).isEqualTo(link.getUrl());
    }

    @Test
    @Rollback
    @Transactional
    void findLinkByUncreatedURL() {
        URI url = URI.create("https://github.com/me/myRep");

        Optional<Link> res = linkRepository.findLinkByURL(url);

        assertThat(res.isEmpty()).isTrue();
    }

    @Test
    @Rollback
    @Transactional
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
            .forEach(it -> linkRepository.insertLink(createLinkWithUniqueUrlWithNum(it)));

        List<Link> res = linkRepository.findAllLinks();

        assertThat(res.size()).isEqualTo(insertCount);
    }

    @Test
    @Rollback
    @Transactional
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
        TgChat chat1 = new TgChat(1L, "a");
        TgChat chat2 = new TgChat(2L, "a");
        chat1 = chatRepository.insertTgChat(chat1).get();
        chat2 = chatRepository.insertTgChat(chat2).get();
        link1.setTgChats(Set.of(chat1, chat2));
        link2.setTgChats(Set.of(chat2));

        link1 = linkRepository.insertLink(link1).get();
        linkRepository.insertLink(link2).get();

        List<Link> res = linkRepository.findLinksByTgChatId(chat1.getChatId());

        assertThat(res.size()).isEqualTo(1);
        assertThat(res.get(0)).isEqualTo(link1);
    }

    @Test
    @Rollback
    @Transactional
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
        chat1 = chatRepository.insertTgChat(chat1).get();
        chat2 = chatRepository.insertTgChat(chat2).get();
        link1.setTgChats(Set.of(chat1, chat2));
        link2.setTgChats(Set.of(chat2));

        link1 = linkRepository.insertLink(link1).get();
        linkRepository.insertLink(link2).get();

        linkRepository.removeLinksByTgChatId(chat2.getId());
        List<Link> res = linkRepository.findAllLinks();
        // link2 should be removed bc only chat tracked it
        link1.setTgChats(Set.of(chat1));   // for clean equals (chat2 was removed)

        assertThat(res.size()).isEqualTo(1);
        assertThat(res.get(0)).isEqualTo(link1);
    }

    @Test
    @Rollback
    @Transactional
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

        linkRepository.insertLink(checkedLink).get();
        uncheckedLink = linkRepository.insertLink(uncheckedLink).get();

        List<Link> res = linkRepository.findAllWhereLastCheckTimeBefore(needLastCheckTime);

        assertThat(res.size()).isEqualTo(1);
        assertThat(res.get(0)).isEqualTo(uncheckedLink);
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
