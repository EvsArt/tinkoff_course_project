package edu.java.service.jooq;

import edu.java.domain.jooqRepository.JooqLinkRepository;
import edu.java.exceptions.LinkNotExistsException;
import edu.java.model.entity.Link;
import edu.java.model.entity.TgChat;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import edu.java.scrapper.JooqIntegrationTest;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@Rollback
@Transactional
class JooqLinkServiceTest extends JooqIntegrationTest {

    @Test
    void addLink() {
        TgChat chat1 = new TgChat(1L, "Chat1");
        chat1 = jooqTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        Link newLink = new Link(URI.create("https://github.com/me/MyRep"), "Link");
        newLink.getTgChats().add(chat1);

        Link res = jooqLinkService.addLink(chat1.getChatId(), newLink.getUrl(), newLink.getName());

        assertThat(res.getUrl()).isEqualTo(newLink.getUrl());
        assertThat(res.getTgChats()).isEqualTo(newLink.getTgChats());
    }

    @Test
    void addExistedLink_ShouldAddChatToIt() {
        TgChat chat1 = new TgChat(1L, "Chat1");
        TgChat chat2 = new TgChat(2L, "Chat2");
        chat1 = jooqTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        chat2 = jooqTgChatService.registerChat(chat2.getChatId(), chat2.getName());

        URI uri = URI.create("https://github.com/me/MyRep");
        Link oldLink = new Link(uri, "Link1");
        oldLink.getTgChats().add(chat1);
        jooqLinkService.addLink(chat1.getChatId(), oldLink.getUrl(), oldLink.getName());

        Link newLink = new Link(uri, "Link2");
        newLink.getTgChats().add(chat2);
        newLink = jooqLinkService.addLink(chat2.getChatId(), newLink.getUrl(), newLink.getName());

        assertThat(newLink.getUrl()).isEqualTo(uri);
        AssertionsForInterfaceTypes.assertThat(newLink.getTgChats()).containsAll(List.of(chat1, chat2));
    }

    @Test
    void addLinkToNotExistedChat_ShouldThrowException() {
        Throwable res =
            catchThrowable(() -> jooqLinkService.addLink(11L, URI.create("https://github.com/me/MyRep"), "MyLink"));

        assertThat(res).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void removeLink() {
        TgChat chat1 = new TgChat(11L, "Chat1");
        chat1 = jooqTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        Link newLink = new Link(URI.create("https://github.com/me/MyRep"), "Link");
        newLink.getTgChats().add(chat1);

        jooqLinkService.addLink(chat1.getChatId(), newLink.getUrl(), newLink.getName());
        jooqLinkService.removeLink(chat1.getChatId(), newLink.getUrl());

        AssertionsForInterfaceTypes.assertThat(jooqLinkRepository.findAllLinks()).isEmpty();
    }

    @Test
    void removeLinkInOneChat_ShouldNotRemoveItInAnotherChat() {
        TgChat chat1 = new TgChat(1L, "Chat1");
        TgChat chat2 = new TgChat(2L, "Chat2");
        chat1 = jooqTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        chat2 = jooqTgChatService.registerChat(chat2.getChatId(), chat2.getName());

        Link newLink = new Link(URI.create("https://github.com/me/MyRep"), "Link");
        newLink.getTgChats().addAll(List.of(chat1, chat2));

        jooqLinkService.addLink(chat1.getChatId(), newLink.getUrl(), newLink.getName());
        jooqLinkService.addLink(chat2.getChatId(), newLink.getUrl(), newLink.getName());
        jooqLinkService.removeLink(chat1.getChatId(), newLink.getUrl());

        List<Link> existLinks = jooqLinkRepository.findAllLinks();

        AssertionsForInterfaceTypes.assertThat(existLinks).hasSize(1);
        AssertionsForInterfaceTypes.assertThat(existLinks.getFirst().getTgChats()).hasSize(1);
        AssertionsForInterfaceTypes.assertThat(existLinks.getFirst().getTgChats()).contains(chat2);
    }

    @Test
    void removeNotExistsLink_ShouldThrowLinkNotExistsException() {
        long tgChatId = 11L;
        URI url = URI.create("https://github.com/me/MyRep");
        jooqTgChatService.registerChat(tgChatId, "");

        Throwable res = catchThrowable(() -> jooqLinkService.removeLink(tgChatId, url));

        assertThat(res).isInstanceOf(LinkNotExistsException.class);
    }

    @Test
    void findAllByTgChatId() {
        TgChat chat1 = new TgChat(12L, "Chat1");
        chat1 = jooqTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        Link newLink = new Link(URI.create("https://github.com/me/MyRep"), "Link");
        newLink.getTgChats().add(chat1);

        newLink = jooqLinkService.addLink(chat1.getChatId(), newLink.getUrl(), newLink.getName());

        List<Link> res = jooqLinkService.findAllByTgChatId(chat1.getChatId());

        AssertionsForInterfaceTypes.assertThat(res).hasSize(1);
        assertThat(res.getFirst()).isEqualTo(newLink);
    }

    @Test
    void findAll() {
        TgChat chat1 = new TgChat(1L, "Chat1");
        TgChat chat2 = new TgChat(2L, "Chat2");
        chat1 = jooqTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        chat2 = jooqTgChatService.registerChat(chat2.getChatId(), chat2.getName());

        Link newLink1 = new Link(URI.create("https://github.com/me/MyRep1"), "Link1");
        Link newLink2 = new Link(URI.create("https://github.com/me/MyRep2"), "Link2");

        newLink1 = jooqLinkService.addLink(chat1.getChatId(), newLink1.getUrl(), newLink1.getName());
        jooqLinkService.addLink(chat1.getChatId(), newLink2.getUrl(), newLink2.getName());
        newLink2 = jooqLinkService.addLink(chat2.getChatId(), newLink2.getUrl(), newLink2.getName());

        List<Link> res = jooqLinkService.findAll();

        AssertionsForInterfaceTypes.assertThat(res).hasSize(2);
        assertThat(res.containsAll(List.of(newLink1, newLink2))).isTrue();
    }

    @Test
    void findAllWhereLastCheckTimeBefore() {
        OffsetDateTime needLastCheckTime = OffsetDateTime.parse("2024-03-15T11:17:31+03:00");
        TgChat chat1 = new TgChat(1L, "Chat1");
        TgChat chat2 = new TgChat(2L, "Chat2");
        chat1 = jooqTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        chat2 = jooqTgChatService.registerChat(chat2.getChatId(), chat2.getName());
        Link checkedLink = new Link(
            URI.create("https://github.com/me/myRep1"),
            "name1"
        );
        Link uncheckedLink = new Link(
            URI.create("https://github.com/me/myRep2"),
            "name2"
        );

        checkedLink = jooqLinkService.addLink(chat1.getChatId(), checkedLink.getUrl(), checkedLink.getName());
        uncheckedLink = jooqLinkService.addLink(chat2.getChatId(), uncheckedLink.getUrl(), uncheckedLink.getName());
        jooqLinkService.setLastCheckTime(checkedLink.getId(), needLastCheckTime.plusDays(1));
        jooqLinkService.setLastCheckTime(uncheckedLink.getId(), needLastCheckTime.minusDays(1));

        List<Link> res = jooqLinkService.findAllWhereLastCheckTimeBefore(needLastCheckTime);

        AssertionsForInterfaceTypes.assertThat(res).hasSize(1);
        assertThat(res.getFirst().getName()).isEqualTo(uncheckedLink.getName());
    }
}
