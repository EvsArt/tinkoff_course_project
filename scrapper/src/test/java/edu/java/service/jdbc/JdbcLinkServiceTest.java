package edu.java.service.jdbc;

import edu.java.domain.jdbcRepository.JdbcLinkRepository;
import edu.java.exceptions.LinkNotExistsException;
import edu.java.model.entity.Link;
import edu.java.model.entity.TgChat;
import edu.java.scrapper.JdbcIntegrationTest;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@Rollback
@Transactional
class JdbcLinkServiceTest extends JdbcIntegrationTest {

    @Autowired
    private JdbcLinkService jdbcLinkService;
    @Autowired
    private JdbcTgChatService jdbcTgChatService;
    @Autowired
    private JdbcLinkRepository jdbcLinkRepository;

    @Test
    void addLink() {
        TgChat chat1 = new TgChat(1L, "Chat1");
        chat1 = jdbcTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        Link newLink = new Link(URI.create("https://github.com/me/MyRep"), "Link");
        newLink.getTgChats().add(chat1);

        Link res = jdbcLinkService.addLink(chat1.getChatId(), newLink.getUrl(), newLink.getName());

        assertThat(res.getUrl()).isEqualTo(newLink.getUrl());
        assertThat(res.getTgChats()).isEqualTo(newLink.getTgChats());
    }

    @Test
    void addExistedLink_ShouldAddChatToIt() {
        TgChat chat1 = new TgChat(1L, "Chat1");
        TgChat chat2 = new TgChat(2L, "Chat2");
        chat1 = jdbcTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        chat2 = jdbcTgChatService.registerChat(chat2.getChatId(), chat2.getName());

        URI uri = URI.create("https://github.com/me/MyRep");
        Link oldLink = new Link(uri, "Link1");
        oldLink.getTgChats().add(chat1);
        jdbcLinkService.addLink(chat1.getChatId(), oldLink.getUrl(), oldLink.getName());

        Link newLink = new Link(uri, "Link2");
        newLink.getTgChats().add(chat2);
        newLink = jdbcLinkService.addLink(chat2.getChatId(), newLink.getUrl(), newLink.getName());

        assertThat(newLink.getUrl()).isEqualTo(uri);
        AssertionsForInterfaceTypes.assertThat(newLink.getTgChats()).containsAll(List.of(chat1, chat2));
    }

    @Test
    void addLinkToNotExistedChat_ShouldThrowException() {
        Throwable res =
            catchThrowable(() -> jdbcLinkService.addLink(11L, URI.create("https://github.com/me/MyRep"), "MyLink"));

        assertThat(res).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void removeLink() {
        TgChat chat1 = new TgChat(11L, "Chat1");
        chat1 = jdbcTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        Link newLink = new Link(URI.create("https://github.com/me/MyRep"), "Link");
        newLink.getTgChats().add(chat1);

        jdbcLinkService.addLink(chat1.getChatId(), newLink.getUrl(), newLink.getName());
        jdbcLinkService.removeLink(chat1.getChatId(), newLink.getUrl());

        AssertionsForInterfaceTypes.assertThat(jdbcLinkRepository.findAllLinks()).isEmpty();
    }

    @Test
    void removeLinkInOneChat_ShouldNotRemoveItInAnotherChat() {
        TgChat chat1 = new TgChat(1L, "Chat1");
        TgChat chat2 = new TgChat(2L, "Chat2");
        chat1 = jdbcTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        chat2 = jdbcTgChatService.registerChat(chat2.getChatId(), chat2.getName());

        Link newLink = new Link(URI.create("https://github.com/me/MyRep"), "Link");
        newLink.getTgChats().addAll(List.of(chat1, chat2));

        jdbcLinkService.addLink(chat1.getChatId(), newLink.getUrl(), newLink.getName());
        jdbcLinkService.addLink(chat2.getChatId(), newLink.getUrl(), newLink.getName());
        jdbcLinkService.removeLink(chat1.getChatId(), newLink.getUrl());

        List<Link> existLinks = jdbcLinkRepository.findAllLinks();

        AssertionsForInterfaceTypes.assertThat(existLinks).hasSize(1);
        AssertionsForInterfaceTypes.assertThat(existLinks.getFirst().getTgChats()).hasSize(1);
        AssertionsForInterfaceTypes.assertThat(existLinks.getFirst().getTgChats()).contains(chat2);
    }

    @Test
    void removeNotExistsLink_ShouldThrowLinkNotExistsException() {
        long tgChatId = 11L;
        URI url = URI.create("https://github.com/me/MyRep");
        jdbcTgChatService.registerChat(tgChatId, "");

        Throwable res = catchThrowable(() -> jdbcLinkService.removeLink(tgChatId, url));

        assertThat(res).isInstanceOf(LinkNotExistsException.class);
    }

    @Test
    void findAllByTgChatId() {
        TgChat chat1 = new TgChat(12L, "Chat1");
        chat1 = jdbcTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        Link newLink = new Link(URI.create("https://github.com/me/MyRep"), "Link");
        newLink.getTgChats().add(chat1);

        newLink = jdbcLinkService.addLink(chat1.getChatId(), newLink.getUrl(), newLink.getName());

        List<Link> res = jdbcLinkService.findAllByTgChatId(chat1.getChatId());

        AssertionsForInterfaceTypes.assertThat(res).hasSize(1);
        assertThat(res.getFirst()).isEqualTo(newLink);
    }

    @Test
    void findAll() {
        TgChat chat1 = new TgChat(1L, "Chat1");
        TgChat chat2 = new TgChat(2L, "Chat2");
        chat1 = jdbcTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        chat2 = jdbcTgChatService.registerChat(chat2.getChatId(), chat2.getName());

        Link newLink1 = new Link(URI.create("https://github.com/me/MyRep1"), "Link1");
        Link newLink2 = new Link(URI.create("https://github.com/me/MyRep2"), "Link2");

        newLink1 = jdbcLinkService.addLink(chat1.getChatId(), newLink1.getUrl(), newLink1.getName());
        jdbcLinkService.addLink(chat1.getChatId(), newLink2.getUrl(), newLink2.getName());
        newLink2 = jdbcLinkService.addLink(chat2.getChatId(), newLink2.getUrl(), newLink2.getName());

        List<Link> res = jdbcLinkService.findAll();

        AssertionsForInterfaceTypes.assertThat(res).hasSize(2);
        AssertionsForInterfaceTypes.assertThat(res).containsAll(List.of(newLink1, newLink2));
    }

    @Test
    void findAllWhereLastCheckTimeBefore() {
        OffsetDateTime needLastCheckTime = OffsetDateTime.parse("2024-03-15T11:17:31+03:00");
        TgChat chat1 = new TgChat(1L, "Chat1");
        TgChat chat2 = new TgChat(2L, "Chat2");
        chat1 = jdbcTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        chat2 = jdbcTgChatService.registerChat(chat2.getChatId(), chat2.getName());
        Link checkedLink = new Link(
            URI.create("https://github.com/me/myRep1"),
            "name1"
        );
        Link uncheckedLink = new Link(
            URI.create("https://github.com/me/myRep2"),
            "name2"
        );

        checkedLink = jdbcLinkService.addLink(chat1.getChatId(), checkedLink.getUrl(), checkedLink.getName());
        uncheckedLink = jdbcLinkService.addLink(chat2.getChatId(), uncheckedLink.getUrl(), uncheckedLink.getName());
        jdbcLinkService.setLastCheckTime(checkedLink.getId(), needLastCheckTime.plusDays(1));
        jdbcLinkService.setLastCheckTime(uncheckedLink.getId(), needLastCheckTime.minusDays(1));

        List<Link> res = jdbcLinkService.findAllWhereLastCheckTimeBefore(needLastCheckTime);

        AssertionsForInterfaceTypes.assertThat(res).hasSize(1);
        assertThat(res.getFirst().getName()).isEqualTo(uncheckedLink.getName());
    }
}
