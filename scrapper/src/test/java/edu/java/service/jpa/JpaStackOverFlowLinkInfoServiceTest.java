package edu.java.service.jpa;

import edu.java.client.StackOverflowClient;
import edu.java.domain.jpaRepository.JpaStackOverFlowLinkInfoRepository;
import edu.java.dto.StackOverflowQuestionResponse;
import edu.java.exceptions.LinkNotExistsException;
import edu.java.model.entity.Link;
import edu.java.model.entity.StackOverFlowLinkInfo;
import edu.java.model.entity.TgChat;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.time.OffsetDateTime;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@TestPropertySource(properties = "app.database-access-type=jpa")
class JpaStackOverFlowLinkInfoServiceTest extends IntegrationTest {

    @Autowired JpaLinkService linkService;
    @Autowired JpaTgChatService tgChatService;
    @Autowired JpaStackOverFlowLinkInfoRepository linkInfoRepository;
    @Autowired JpaStackOverFlowLinkInfoService linkInfoService;
    @MockBean StackOverflowClient client;

    @Test
    @Transactional
    @Rollback
    void findLinkInfoByLinkId() {
        TgChat chat = new TgChat(11L, "name");
        Link link = new Link(URI.create("http://github.com/rep/name"), "Link1");

        chat = tgChatService.registerChat(chat.getChatId(), chat.getName());
        link = linkService.addLink(chat.getChatId(), link.getUrl(), link.getName());

        StackOverFlowLinkInfo linkInfo = new StackOverFlowLinkInfo(link, 999);
        linkInfoRepository.save(linkInfo);
        StackOverFlowLinkInfo res = linkInfoService.findLinkInfoByLinkId(link.getId());

        assertThat(res).isEqualTo(linkInfo);
    }

    @Test
    @Transactional
    @Rollback
    void findLinkInfoByNotExistsLinkId_shouldThrow() {
        Throwable finding = catchThrowable(() -> linkInfoService.findLinkInfoByLinkId(123L));

        assertThat(finding).isInstanceOf(LinkNotExistsException.class);
    }

    @Test
    @Transactional
    @Rollback
    void findLinkInfoByLinkUrl() {
        TgChat chat = new TgChat(11L, "name");
        Link link = new Link(URI.create("http://github.com/rep/name"), "Link1");

        chat = tgChatService.registerChat(chat.getChatId(), chat.getName());
        link = linkService.addLink(chat.getChatId(), link.getUrl(), link.getName());

        StackOverFlowLinkInfo linkInfo = new StackOverFlowLinkInfo(link, 999);
        linkInfoRepository.save(linkInfo);
        StackOverFlowLinkInfo res = linkInfoService.findLinkInfoByLinkUrl(link.getUrl());

        assertThat(res).isEqualTo(linkInfo);
    }

    @Test
    @Transactional
    @Rollback
    void findLinkInfoByNotExistsUrl_shouldThrowLinkNotExistsException() {
        Throwable finding = catchThrowable(() -> linkInfoService.findLinkInfoByLinkUrl(URI.create("MyUrl")));

        assertThat(finding).isInstanceOf(LinkNotExistsException.class);
    }

    @Test
    @Transactional
    @Rollback
    void addLinkInfo() {
        TgChat chat = new TgChat(11L, "name");
        Link link = new Link(URI.create("https://stackoverflow.com/questions/111111/name"), "Link1");
        StackOverflowQuestionResponse questionResponse = new StackOverflowQuestionResponse(
            1L,
            "Question",
            OffsetDateTime.now(),
            999
        );
        Mockito.when(client.getQuestion(Mockito.any())).thenReturn(Mono.just(questionResponse));

        chat = tgChatService.registerChat(chat.getChatId(), chat.getName());
        link = linkService.addLink(chat.getChatId(), link.getUrl(), link.getName());
        StackOverFlowLinkInfo linkInfo = new StackOverFlowLinkInfo(link, questionResponse.answerCount());

        linkInfoService.addLinkInfo(link);

        StackOverFlowLinkInfo res = linkInfoService.findLinkInfoByLinkUrl(link.getUrl());
        linkInfo.setId(res.getId());    // for clean equals

        assertThat(res).isEqualTo(linkInfo);
    }

    @Test
    @Transactional
    @Rollback
    void updateLinkInfo() {
        TgChat chat = new TgChat(11L, "name");
        Link link = new Link(URI.create("http://github.com/rep/name"), "Link1");

        chat = tgChatService.registerChat(chat.getChatId(), chat.getName());
        link = linkService.addLink(chat.getChatId(), link.getUrl(), link.getName());
        StackOverFlowLinkInfo oldLinkInfo = new StackOverFlowLinkInfo(link, 999);
        StackOverFlowLinkInfo newLinkInfo = new StackOverFlowLinkInfo(link, 666);

        oldLinkInfo = linkInfoRepository.save(oldLinkInfo);
        newLinkInfo.setId(oldLinkInfo.getId());     // setting id needs for updating instead of saving
        linkInfoRepository.save(newLinkInfo);

        StackOverFlowLinkInfo res = linkInfoService.findLinkInfoByLinkUrl(link.getUrl());

        assertThat(res).isEqualTo(newLinkInfo);
    }

    @Test
    @Transactional
    @Rollback
    void removeLinkInfoByLink_shouldReturnRemovedLink() {
        TgChat chat = new TgChat(11L, "name");
        Link link = new Link(URI.create("http://github.com/rep/name"), "Link1");

        chat = tgChatService.registerChat(chat.getChatId(), chat.getName());
        link = linkService.addLink(chat.getChatId(), link.getUrl(), link.getName());
        StackOverFlowLinkInfo linkInfo = new StackOverFlowLinkInfo(link, 999);

        linkInfo = linkInfoRepository.save(linkInfo);
        StackOverFlowLinkInfo deletedLinkInfo = linkInfoService.removeLinkInfoByLink(link);

        Throwable findingDeletedLinkInfo =
            catchThrowable(() -> linkInfoService.findLinkInfoByLinkUrl(URI.create("http://github.com/rep/name")));

        assertThat(deletedLinkInfo).isEqualTo(linkInfo);
        assertThat(findingDeletedLinkInfo).isInstanceOf(LinkNotExistsException.class);
    }

    @Test
    @Transactional
    @Rollback
    void removeNotExistedLinkInfo_shouldThrowLinkNotExistsException() {
        Throwable deletingLink =
            catchThrowable(() -> linkInfoService.removeLinkInfoByLink(new Link(URI.create("a"), "b")));

        assertThat(deletingLink).isInstanceOf(LinkNotExistsException.class);
    }

}
