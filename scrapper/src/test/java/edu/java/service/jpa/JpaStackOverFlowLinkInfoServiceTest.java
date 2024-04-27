package edu.java.service.jpa;

import edu.java.servicesClient.StackOverflowClient;
import edu.java.dto.stackoverflow.StackOverflowQuestionResponse;
import edu.java.exceptions.LinkNotExistsException;
import edu.java.model.entity.Link;
import edu.java.model.entity.StackOverFlowLinkInfo;
import edu.java.model.entity.TgChat;
import edu.java.scrapper.JpaIntegrationTest;
import java.net.URI;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@Rollback
@Transactional
class JpaStackOverFlowLinkInfoServiceTest extends JpaIntegrationTest {

    @MockBean StackOverflowClient client;

    @Test
    void findLinkInfoByLinkId() {
        TgChat chat = new TgChat(11L, "name");
        Link link = new Link(URI.create("http://github.com/rep/name"), "Link1");

        chat = jpaTgChatService.registerChat(chat.getChatId(), chat.getName());
        link = jpaLinkService.addLink(chat.getChatId(), link.getUrl(), link.getName());

        StackOverFlowLinkInfo linkInfo = new StackOverFlowLinkInfo(link, 999);
        jpaStackOverFlowLinkInfoRepository.save(linkInfo);
        StackOverFlowLinkInfo res = jpaStackOverFlowLinkInfoService.findLinkInfoByLinkId(link.getId());

        assertThat(res).isEqualTo(linkInfo);
    }

    @Test
    void findLinkInfoByNotExistsLinkId_shouldThrow() {
        Throwable finding = catchThrowable(() -> jpaStackOverFlowLinkInfoService.findLinkInfoByLinkId(123L));

        assertThat(finding).isInstanceOf(LinkNotExistsException.class);
    }

    @Test
    void findLinkInfoByLinkUrl() {
        TgChat chat = new TgChat(11L, "name");
        Link link = new Link(URI.create("http://github.com/rep/name"), "Link1");

        chat = jpaTgChatService.registerChat(chat.getChatId(), chat.getName());
        link = jpaLinkService.addLink(chat.getChatId(), link.getUrl(), link.getName());

        StackOverFlowLinkInfo linkInfo = new StackOverFlowLinkInfo(link, 999);
        jpaStackOverFlowLinkInfoRepository.save(linkInfo);
        StackOverFlowLinkInfo res = jpaStackOverFlowLinkInfoService.findLinkInfoByLinkUrl(link.getUrl());

        assertThat(res).isEqualTo(linkInfo);
    }

    @Test
    void findLinkInfoByNotExistsUrl_shouldThrowLinkNotExistsException() {
        Throwable finding =
            catchThrowable(() -> jpaStackOverFlowLinkInfoService.findLinkInfoByLinkUrl(URI.create("MyUrl")));

        assertThat(finding).isInstanceOf(LinkNotExistsException.class);
    }

    @Test
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

        chat = jpaTgChatService.registerChat(chat.getChatId(), chat.getName());
        link = jpaLinkService.addLink(chat.getChatId(), link.getUrl(), link.getName());
        StackOverFlowLinkInfo linkInfo = new StackOverFlowLinkInfo(link, questionResponse.answerCount());

        jpaStackOverFlowLinkInfoService.addLinkInfo(link);

        StackOverFlowLinkInfo res = jpaStackOverFlowLinkInfoService.findLinkInfoByLinkUrl(link.getUrl());
        linkInfo.setId(res.getId());    // for clean equals

        assertThat(res).isEqualTo(linkInfo);
    }

    @Test
    void updateLinkInfo() {
        TgChat chat = new TgChat(11L, "name");
        Link link = new Link(URI.create("http://github.com/rep/name"), "Link1");

        chat = jpaTgChatService.registerChat(chat.getChatId(), chat.getName());
        link = jpaLinkService.addLink(chat.getChatId(), link.getUrl(), link.getName());
        StackOverFlowLinkInfo oldLinkInfo = new StackOverFlowLinkInfo(link, 999);
        StackOverFlowLinkInfo newLinkInfo = new StackOverFlowLinkInfo(link, 666);

        oldLinkInfo = jpaStackOverFlowLinkInfoRepository.save(oldLinkInfo);
        newLinkInfo.setId(oldLinkInfo.getId());     // setting id needs for updating instead of saving
        jpaStackOverFlowLinkInfoRepository.save(newLinkInfo);

        StackOverFlowLinkInfo res = jpaStackOverFlowLinkInfoService.findLinkInfoByLinkUrl(link.getUrl());

        assertThat(res).isEqualTo(newLinkInfo);
    }

    @Test
    void removeLinkInfoByLink_shouldReturnRemovedLink() {
        TgChat chat = new TgChat(11L, "name");
        Link link = new Link(URI.create("http://github.com/rep/name"), "Link1");

        chat = jpaTgChatService.registerChat(chat.getChatId(), chat.getName());
        link = jpaLinkService.addLink(chat.getChatId(), link.getUrl(), link.getName());
        StackOverFlowLinkInfo linkInfo = new StackOverFlowLinkInfo(link, 999);

        linkInfo = jpaStackOverFlowLinkInfoRepository.save(linkInfo);
        StackOverFlowLinkInfo deletedLinkInfo = jpaStackOverFlowLinkInfoService.removeLinkInfoByLink(link);

        Throwable findingDeletedLinkInfo =
            catchThrowable(() -> jpaStackOverFlowLinkInfoService.findLinkInfoByLinkUrl(URI.create(
                "http://github.com/rep/name")));

        assertThat(deletedLinkInfo).isEqualTo(linkInfo);
        assertThat(findingDeletedLinkInfo).isInstanceOf(LinkNotExistsException.class);
    }

    @Test
    void removeNotExistedLinkInfo_shouldThrowLinkNotExistsException() {
        Throwable deletingLink =
            catchThrowable(() -> jpaStackOverFlowLinkInfoService.removeLinkInfoByLink(new Link(URI.create("a"), "b")));

        assertThat(deletingLink).isInstanceOf(LinkNotExistsException.class);
    }

}
