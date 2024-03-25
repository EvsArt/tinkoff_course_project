package edu.java.service.jpa;

import edu.java.client.GitHubClient;
import edu.java.domain.jpaRepository.JpaGitHubLinkInfoRepository;
import edu.java.dto.GitHubRepoEventResponse;
import edu.java.exceptions.LinkNotExistsException;
import edu.java.model.entity.GitHubLinkInfo;
import edu.java.model.entity.Link;
import edu.java.model.entity.TgChat;
import edu.java.scrapper.JpaIntegrationTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import java.net.URI;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@Rollback
@Transactional
class JpaGitHubLinkInfoServiceTest extends JpaIntegrationTest {

    @MockBean GitHubClient client;

    @Test
    @Rollback
    void findLinkInfoByLinkId() {
        TgChat chat = new TgChat(11L, "name");
        Link link = new Link(URI.create("http://github.com/rep/name"), "Link1");

        chat = jpaTgChatService.registerChat(chat.getChatId(), chat.getName());
        link = jpaLinkService.addLink(chat.getChatId(), link.getUrl(), link.getName());

        GitHubLinkInfo linkInfo = new GitHubLinkInfo(link, 999L);
        jpaGitHubLinkInfoRepository.save(linkInfo);
        GitHubLinkInfo res = jpaGitHubLinkInfoService.findLinkInfoByLinkId(link.getId());

        assertThat(res).isEqualTo(linkInfo);
    }

    @Test
    @Rollback
    void findLinkInfoByNotExistsLinkId_shouldThrow() {
        Throwable finding = catchThrowable(() -> jpaGitHubLinkInfoService.findLinkInfoByLinkId(123L));

        assertThat(finding).isInstanceOf(LinkNotExistsException.class);
    }

    @Test
    @Rollback
    void findLinkInfoByLinkUrl() {
        TgChat chat = new TgChat(11L, "name");
        Link link = new Link(URI.create("http://github.com/rep/name"), "Link1");

        chat = jpaTgChatService.registerChat(chat.getChatId(), chat.getName());
        link = jpaLinkService.addLink(chat.getChatId(), link.getUrl(), link.getName());

        GitHubLinkInfo linkInfo = new GitHubLinkInfo(link, 999L);
        jpaGitHubLinkInfoRepository.save(linkInfo);
        GitHubLinkInfo res = jpaGitHubLinkInfoService.findLinkInfoByLinkUrl(link.getUrl());

        assertThat(res).isEqualTo(linkInfo);
    }

    @Test
    @Rollback
    void findLinkInfoByNotExistsUrl_shouldThrowLinkNotExistsException() {
        Throwable finding = catchThrowable(() -> jpaGitHubLinkInfoService.findLinkInfoByLinkUrl(URI.create("MyUrl")));

        assertThat(finding).isInstanceOf(LinkNotExistsException.class);
    }

    @Test
    @Rollback
    void addLinkInfo() {
        long lastEventId = 999L;
        TgChat chat = new TgChat(11L, "name");
        Link link = new Link(URI.create("http://github.com/rep/name"), "Link1");
        GitHubRepoEventResponse eventResponse = new GitHubRepoEventResponse(lastEventId, "PushType");
        Mockito.when(client.getLastRepositoryEvent(Mockito.any())).thenReturn(Mono.just(eventResponse));

        chat = jpaTgChatService.registerChat(chat.getChatId(), chat.getName());
        link = jpaLinkService.addLink(chat.getChatId(), link.getUrl(), link.getName());
        GitHubLinkInfo linkInfo = new GitHubLinkInfo(link, eventResponse.getId()
        );

        jpaGitHubLinkInfoService.addLinkInfo(link);

        GitHubLinkInfo res = jpaGitHubLinkInfoService.findLinkInfoByLinkUrl(link.getUrl());
        linkInfo.setId(res.getId());    // for clean equals

        assertThat(res).isEqualTo(linkInfo);
    }

    @Test
    @Rollback
    void updateLinkInfo() {
        TgChat chat = new TgChat(11L, "name");
        Link link = new Link(URI.create("http://github.com/rep/name"), "Link1");

        chat = jpaTgChatService.registerChat(chat.getChatId(), chat.getName());
        link = jpaLinkService.addLink(chat.getChatId(), link.getUrl(), link.getName());
        GitHubLinkInfo oldLinkInfo = new GitHubLinkInfo(link, 999L);
        GitHubLinkInfo newLinkInfo = new GitHubLinkInfo(link, 666L);

        oldLinkInfo = jpaGitHubLinkInfoRepository.save(oldLinkInfo);
        newLinkInfo.setId(oldLinkInfo.getId());     // setting id needs for updating instead of saving
        jpaGitHubLinkInfoRepository.save(newLinkInfo);

        GitHubLinkInfo res = jpaGitHubLinkInfoService.findLinkInfoByLinkUrl(link.getUrl());

        assertThat(res).isEqualTo(newLinkInfo);
    }

    @Test
    @Rollback
    void removeLinkInfoByLink_shouldReturnRemovedLink() {
        TgChat chat = new TgChat(11L, "name");
        Link link = new Link(URI.create("http://github.com/rep/name"), "Link1");

        chat = jpaTgChatService.registerChat(chat.getChatId(), chat.getName());
        link = jpaLinkService.addLink(chat.getChatId(), link.getUrl(), link.getName());
        GitHubLinkInfo linkInfo = new GitHubLinkInfo(link, 999L);

        linkInfo = jpaGitHubLinkInfoRepository.save(linkInfo);
        GitHubLinkInfo deletedLinkInfo = jpaGitHubLinkInfoService.removeLinkInfoByLink(link);

        Throwable findingDeletedLinkInfo =
            catchThrowable(() -> jpaGitHubLinkInfoService.findLinkInfoByLinkUrl(URI.create("http://github.com/rep/name")));

        assertThat(deletedLinkInfo).isEqualTo(linkInfo);
        assertThat(findingDeletedLinkInfo).isInstanceOf(LinkNotExistsException.class);
    }

    @Test
    @Rollback
    void removeNotExistedLinkInfo_shouldThrowLinkNotExistsException() {
        Throwable deletingLink =
            catchThrowable(() -> jpaGitHubLinkInfoService.removeLinkInfoByLink(new Link(URI.create("a"), "b")));

        assertThat(deletingLink).isInstanceOf(LinkNotExistsException.class);
    }

}
