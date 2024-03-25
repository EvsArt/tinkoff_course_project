package edu.java.service.jpa;

import edu.java.domain.jpaRepository.JpaLinkRepository;
import edu.java.exceptions.LinkNotExistsException;
import edu.java.model.entity.Link;
import edu.java.model.entity.TgChat;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import edu.java.scrapper.JpaIntegrationTest;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@Rollback
@Transactional
class JpaLinkServiceTest extends JpaIntegrationTest {

    @Test
    void addLink() {
        TgChat chat1 = new TgChat(1L, "Chat1");
        chat1 = jpaTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        Link newLink = new Link(URI.create("https://github.com/me/MyRep"), "Link");
        newLink.getTgChats().add(chat1);

        Link res = jpaLinkService.addLink(chat1.getChatId(), newLink.getUrl(), newLink.getName());

        assertThat(res.getUrl()).isEqualTo(newLink.getUrl());
        assertThat(res.getTgChats()).isEqualTo(newLink.getTgChats());
    }

    @Test
    void addExistedLink_ShouldAddChatToIt() {
        TgChat chat1 = new TgChat(1L, "Chat1");
        TgChat chat2 = new TgChat(2L, "Chat2");
        chat1 = jpaTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        chat2 = jpaTgChatService.registerChat(chat2.getChatId(), chat2.getName());

        URI uri = URI.create("https://github.com/me/MyRep");
        Link oldLink = new Link(uri, "Link1");
        oldLink.getTgChats().add(chat1);
        jpaLinkService.addLink(chat1.getChatId(), oldLink.getUrl(), oldLink.getName());

        Link newLink = new Link(uri, "Link2");
        newLink.getTgChats().add(chat2);
        newLink = jpaLinkService.addLink(chat2.getChatId(), newLink.getUrl(), newLink.getName());

        assertThat(newLink.getUrl()).isEqualTo(uri);
        AssertionsForInterfaceTypes.assertThat(newLink.getTgChats()).containsAll(List.of(chat1, chat2));
    }

    @Test
    void addLinkToNotExistedChat_ShouldThrowException() {
        Throwable res =
            catchThrowable(() -> jpaLinkService.addLink(11L, URI.create("https://github.com/me/MyRep"), "MyLink"));

        assertThat(res).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void removeLink() {
        TgChat chat1 = new TgChat(11L, "Chat1");
        chat1 = jpaTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        Link newLink = new Link(URI.create("https://github.com/me/MyRep"), "Link");
        newLink.getTgChats().add(chat1);

        jpaLinkService.addLink(chat1.getChatId(), newLink.getUrl(), newLink.getName());
        jpaLinkService.removeLink(chat1.getChatId(), newLink.getUrl());

        AssertionsForInterfaceTypes.assertThat(jpaLinkRepository.findAll()).isEmpty();
    }

    @Test
    void removeLinkInOneChat_ShouldNotRemoveItInAnotherChat() {
        TgChat chat1 = new TgChat(1L, "Chat1");
        TgChat chat2 = new TgChat(2L, "Chat2");
        chat1 = jpaTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        chat2 = jpaTgChatService.registerChat(chat2.getChatId(), chat2.getName());

        Link newLink = new Link(URI.create("https://github.com/me/MyRep"), "Link");
        newLink.getTgChats().addAll(List.of(chat1, chat2));

        jpaLinkService.addLink(chat1.getChatId(), newLink.getUrl(), newLink.getName());
        jpaLinkService.addLink(chat2.getChatId(), newLink.getUrl(), newLink.getName());
        jpaLinkService.removeLink(chat1.getChatId(), newLink.getUrl());

        List<Link> existLinks = jpaLinkRepository.findAll();

        AssertionsForInterfaceTypes.assertThat(existLinks).hasSize(1);
        AssertionsForInterfaceTypes.assertThat(existLinks.getFirst().getTgChats()).hasSize(1);
        AssertionsForInterfaceTypes.assertThat(existLinks.getFirst().getTgChats()).contains(chat2);
    }

    @Test
    void removeNotExistsLink_ShouldThrowLinkNotExistsException() {
        long tgChatId = 11L;
        URI url = URI.create("https://github.com/me/MyRep");
        jpaTgChatService.registerChat(tgChatId, "");

        Throwable res = catchThrowable(() -> jpaLinkService.removeLink(tgChatId, url));

        assertThat(res).isInstanceOf(LinkNotExistsException.class);
    }

    @Test
    void findAllByTgChatId() {
        TgChat chat1 = new TgChat(12L, "Chat1");
        chat1 = jpaTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        Link newLink = new Link(URI.create("https://github.com/me/MyRep"), "Link");
        newLink.getTgChats().add(chat1);

        newLink = jpaLinkService.addLink(chat1.getChatId(), newLink.getUrl(), newLink.getName());

        List<Link> res = jpaLinkService.findAllByTgChatId(chat1.getChatId());

        AssertionsForInterfaceTypes.assertThat(res).hasSize(1);
        assertThat(res.getFirst()).isEqualTo(newLink);
    }

    @Test
    void findAll() {
        TgChat chat1 = new TgChat(1L, "Chat1");
        TgChat chat2 = new TgChat(2L, "Chat2");
        chat1 = jpaTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        chat2 = jpaTgChatService.registerChat(chat2.getChatId(), chat2.getName());

        Link newLink1 = new Link(URI.create("https://github.com/me/MyRep1"), "Link1");
        Link newLink2 = new Link(URI.create("https://github.com/me/MyRep2"), "Link2");

        newLink1 = jpaLinkService.addLink(chat1.getChatId(), newLink1.getUrl(), newLink1.getName());
        jpaLinkService.addLink(chat1.getChatId(), newLink2.getUrl(), newLink2.getName());
        newLink2 = jpaLinkService.addLink(chat2.getChatId(), newLink2.getUrl(), newLink2.getName());

        List<Link> res = jpaLinkService.findAll();

        AssertionsForInterfaceTypes.assertThat(res).hasSize(2);
        AssertionsForInterfaceTypes.assertThat(res).containsAll(List.of(newLink1, newLink2));
    }

    @Test
    void findAllWhereLastCheckTimeBefore() {
        OffsetDateTime needLastCheckTime = OffsetDateTime.parse("2024-03-15T11:17:31+03:00");
        TgChat chat1 = new TgChat(1L, "Chat1");
        TgChat chat2 = new TgChat(2L, "Chat2");
        chat1 = jpaTgChatService.registerChat(chat1.getChatId(), chat1.getName());
        chat2 = jpaTgChatService.registerChat(chat2.getChatId(), chat2.getName());
        Link checkedLink = new Link(
            URI.create("https://github.com/me/myRep1"),
            "name1"
        );
        Link uncheckedLink = new Link(
            URI.create("https://github.com/me/myRep2"),
            "name2"
        );

        checkedLink = jpaLinkService.addLink(chat1.getChatId(), checkedLink.getUrl(), checkedLink.getName());
        uncheckedLink = jpaLinkService.addLink(chat2.getChatId(), uncheckedLink.getUrl(), uncheckedLink.getName());
        jpaLinkService.setLastCheckTime(checkedLink.getId(), needLastCheckTime.plusDays(1));
        jpaLinkService.setLastCheckTime(uncheckedLink.getId(), needLastCheckTime.minusDays(1));

        List<Link> res = jpaLinkService.findAllWhereLastCheckTimeBefore(needLastCheckTime);

        AssertionsForInterfaceTypes.assertThat(res).hasSize(1);
        assertThat(res.getFirst().getName()).isEqualTo(uncheckedLink.getName());
    }
}
