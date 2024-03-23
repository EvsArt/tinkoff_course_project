package edu.java.service.jooq;

import edu.java.domain.jooqRepository.JooqLinkRepository;
import edu.java.exceptions.LinkNotExistsException;
import edu.java.model.entity.Link;
import edu.java.model.entity.TgChat;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@TestPropertySource(properties="app.database-access-type=jooq")
class JooqLinkServiceTest extends IntegrationTest {

    @Autowired
    private JooqLinkService linkService;
    @Autowired
    private JooqTgChatService chatService;
    @Autowired
    private JooqLinkRepository linkRepository;

    @Test
    @Transactional
    @Rollback
    void addLink() {
        TgChat chat1 = new TgChat(1L, "Chat1");
        chat1 = chatService.registerChat(chat1.getChatId(), chat1.getName());
        Link newLink = new Link(URI.create("https://github.com/me/MyRep"), "Link");
        newLink.getTgChats().add(chat1);

        Link res = linkService.addLink(chat1.getChatId(), newLink.getUrl(), newLink.getName());

        assertThat(res.getUrl()).isEqualTo(newLink.getUrl());
        assertThat(res.getTgChats()).isEqualTo(newLink.getTgChats());
    }

    @Test
    @Transactional
    @Rollback
    void addExistedLink_ShouldAddChatToIt() {
        TgChat chat1 = new TgChat(1L, "Chat1");
        TgChat chat2 = new TgChat(2L, "Chat2");
        chat1 = chatService.registerChat(chat1.getChatId(), chat1.getName());
        chat2 = chatService.registerChat(chat2.getChatId(), chat2.getName());

        URI uri = URI.create("https://github.com/me/MyRep");
        Link oldLink = new Link(uri, "Link1");
        oldLink.getTgChats().add(chat1);
        linkService.addLink(chat1.getChatId(), oldLink.getUrl(), oldLink.getName());

        Link newLink = new Link(uri, "Link2");
        newLink.getTgChats().add(chat2);
        newLink = linkService.addLink(chat2.getChatId(), newLink.getUrl(), newLink.getName());

        assertThat(newLink.getUrl()).isEqualTo(uri);
        assertThat(newLink.getTgChats().containsAll(List.of(chat1, chat2))).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void addLinkToNotExistedChat_ShouldThrowException() {
        Throwable res =
            catchThrowable(() -> linkService.addLink(11L, URI.create("https://github.com/me/MyRep"), "MyLink"));

        assertThat(res).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Transactional
    @Rollback
    void removeLink() {
        TgChat chat1 = new TgChat(11L, "Chat1");
        chat1 = chatService.registerChat(chat1.getChatId(), chat1.getName());
        Link newLink = new Link(URI.create("https://github.com/me/MyRep"), "Link");
        newLink.getTgChats().add(chat1);

        linkService.addLink(chat1.getChatId(), newLink.getUrl(), newLink.getName());
        linkService.removeLink(chat1.getChatId(), newLink.getUrl());

        assertThat(linkRepository.findAllLinks().isEmpty()).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void removeLinkInOneChat_ShouldNotRemoveItInAnotherChat() {
        TgChat chat1 = new TgChat(1L, "Chat1");
        TgChat chat2 = new TgChat(2L, "Chat2");
        chat1 = chatService.registerChat(chat1.getChatId(), chat1.getName());
        chat2 = chatService.registerChat(chat2.getChatId(), chat2.getName());

        Link newLink = new Link(URI.create("https://github.com/me/MyRep"), "Link");
        newLink.getTgChats().addAll(List.of(chat1, chat2));

        linkService.addLink(chat1.getChatId(), newLink.getUrl(), newLink.getName());
        linkService.addLink(chat2.getChatId(), newLink.getUrl(), newLink.getName());
        linkService.removeLink(chat1.getChatId(), newLink.getUrl());

        List<Link> existLinks = linkRepository.findAllLinks();

        assertThat(existLinks.size()).isEqualTo(1);
        assertThat(existLinks.get(0).getTgChats().size()).isEqualTo(1);
        assertThat(existLinks.get(0).getTgChats().contains(chat2)).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void removeNotExistsLink_ShouldThrowLinkNotExistsException() {
        long tgChatId = 11L;
        URI url = URI.create("https://github.com/me/MyRep");
        chatService.registerChat(tgChatId, "");

        Throwable res = catchThrowable(() -> linkService.removeLink(tgChatId, url));

        assertThat(res).isInstanceOf(LinkNotExistsException.class);
    }

    @Test
    @Transactional
    @Rollback
    void findAllByTgChatId() {
        TgChat chat1 = new TgChat(12L, "Chat1");
        chat1 = chatService.registerChat(chat1.getChatId(), chat1.getName());
        Link newLink = new Link(URI.create("https://github.com/me/MyRep"), "Link");
        newLink.getTgChats().add(chat1);

        newLink = linkService.addLink(chat1.getChatId(), newLink.getUrl(), newLink.getName());

        List<Link> res = linkService.findAllByTgChatId(chat1.getChatId());

        AssertionsForInterfaceTypes.assertThat(res).hasSize(1);
        assertThat(res.get(0)).isEqualTo(newLink);
    }

    @Test
    @Transactional
    @Rollback
    void findAll() {
        TgChat chat1 = new TgChat(1L, "Chat1");
        TgChat chat2 = new TgChat(2L, "Chat2");
        chat1 = chatService.registerChat(chat1.getChatId(), chat1.getName());
        chat2 = chatService.registerChat(chat2.getChatId(), chat2.getName());

        Link newLink1 = new Link(URI.create("https://github.com/me/MyRep1"), "Link1");
        Link newLink2 = new Link(URI.create("https://github.com/me/MyRep2"), "Link2");

        newLink1 = linkService.addLink(chat1.getChatId(), newLink1.getUrl(), newLink1.getName());
        linkService.addLink(chat1.getChatId(), newLink2.getUrl(), newLink2.getName());
        newLink2 = linkService.addLink(chat2.getChatId(), newLink2.getUrl(), newLink2.getName());

        List<Link> res = linkService.findAll();

        assertThat(res.size()).isEqualTo(2);
        assertThat(res.containsAll(List.of(newLink1, newLink2))).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void findAllWhereLastCheckTimeBefore() {
        OffsetDateTime needLastCheckTime = OffsetDateTime.parse("2024-03-15T11:17:31+03:00");
        TgChat chat1 = new TgChat(1L, "Chat1");
        TgChat chat2 = new TgChat(2L, "Chat2");
        chat1 = chatService.registerChat(chat1.getChatId(), chat1.getName());
        chat2 = chatService.registerChat(chat2.getChatId(), chat2.getName());
        Link checkedLink = new Link(
            URI.create("https://github.com/me/myRep1"),
            "name1"
        );
        Link uncheckedLink = new Link(
            URI.create("https://github.com/me/myRep2"),
            "name2"
        );

        checkedLink = linkService.addLink(chat1.getChatId(), checkedLink.getUrl(), checkedLink.getName());
        uncheckedLink = linkService.addLink(chat2.getChatId(), uncheckedLink.getUrl(), uncheckedLink.getName());
        linkService.setLastCheckTime(checkedLink.getId(), needLastCheckTime.plusDays(1));
        linkService.setLastCheckTime(uncheckedLink.getId(), needLastCheckTime.minusDays(1));

        List<Link> res = linkService.findAllWhereLastCheckTimeBefore(needLastCheckTime);

        assertThat(res.size()).isEqualTo(1);
        assertThat(res.get(0).getName()).isEqualTo(uncheckedLink.getName());
    }
}
