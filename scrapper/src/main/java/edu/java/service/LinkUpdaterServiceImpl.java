package edu.java.service;

import edu.java.dto.github.GitHubRepoEventResponse;
import edu.java.dto.github.GitHubRepoRequest;
import edu.java.dto.stackoverflow.StackOverflowQuestionRequest;
import edu.java.dto.stackoverflow.StackOverflowQuestionResponse;
import edu.java.exceptions.status.ForbiddenException;
import edu.java.model.LinkUpdateInfo;
import edu.java.model.entity.GitHubLinkInfo;
import edu.java.model.entity.Link;
import edu.java.model.entity.StackOverFlowLinkInfo;
import edu.java.servicesClient.GitHubClient;
import edu.java.servicesClient.StackOverflowClient;
import edu.java.servicesClient.SupportedApi;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LinkUpdaterServiceImpl implements LinkUpdaterService {

    private final LinksParsingService linksParsingService;
    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;
    private final LinkService linkService;
    private final GitHubLinkInfoService gitHubLinkInfoService;
    private final StackOverFlowLinkInfoService stackOverFlowLinkInfoService;

    @Autowired
    public LinkUpdaterServiceImpl(
        GitHubClient gitHubClient,
        StackOverflowClient stackOverflowClient,
        LinksParsingService linksParsingService,
        LinkService linkService,
        GitHubLinkInfoService gitHubLinkInfoService,
        StackOverFlowLinkInfoService stackOverFlowLinkInfoService
    ) {
        this.gitHubClient = gitHubClient;
        this.stackOverflowClient = stackOverflowClient;
        this.linksParsingService = linksParsingService;
        this.linkService = linkService;
        this.gitHubLinkInfoService = gitHubLinkInfoService;
        this.stackOverFlowLinkInfoService = stackOverFlowLinkInfoService;
    }

    @Override
    public LinkUpdateInfo checkUpdates(Link link) {
        SupportedApi api = SupportedApi.getApiByLink(link.getUrl().toString());
        linkService.setLastCheckTime(link.getId(), OffsetDateTime.now());
        try {
            return switch (api) {
                case GITHUB_REPO -> checkGitHubRepoUpdates(link);
                case STACKOVERFLOW_QUESTION -> checkStackOverFlowQuestionUpdates(link);
            };
        } catch (ForbiddenException e) {
            log.error("Request forbidden on {}", api);
            return LinkUpdateInfo.updateInfoWithoutUpdate();
        }
    }

    public LinkUpdateInfo checkGitHubRepoUpdates(Link link) {

        List<LinkUpdateInfo> foundUpdates = new ArrayList<>();
        foundUpdates.add(checkNewGitHubEvents(link));

        return foundUpdates.stream()
            .filter(LinkUpdateInfo::isUpdated)
            .findFirst()
            .orElse(LinkUpdateInfo.updateInfoWithoutUpdate());
    }

    public LinkUpdateInfo checkStackOverFlowQuestionUpdates(Link link) {
        List<LinkUpdateInfo> foundUpdates = new ArrayList<>();
        foundUpdates.add(checkNewStackOverFlowAnswers(link));

        return foundUpdates.stream()
            .filter(LinkUpdateInfo::isUpdated)
            .findFirst()
            .orElse(LinkUpdateInfo.updateInfoWithoutUpdate());
    }

    private LinkUpdateInfo checkNewStackOverFlowAnswers(Link link) {
        StackOverflowQuestionRequest request =
            linksParsingService.getStackOverFlowQuestionRequestByLink(link.getUrl().toString());

        StackOverflowQuestionResponse response = stackOverflowClient.getQuestion(request).block();

        StackOverFlowLinkInfo linkInfo = stackOverFlowLinkInfoService.findLinkInfoByLinkUrl(link.getUrl());

        int responseEventsCount = response.answerCount();
        int savedEventsCount = linkInfo.getAnswersCount();

        if (responseEventsCount == savedEventsCount) {
            linkInfo.setAnswersCount(responseEventsCount);
            stackOverFlowLinkInfoService.updateLinkInfo(linkInfo.getId(), linkInfo);
            return LinkUpdateInfo.updateInfoWithoutUpdate();
        }
        return LinkUpdateInfo.updateInfoWithUpdate("There is a new answer!", link);
    }

    private LinkUpdateInfo checkNewGitHubEvents(Link link) {
        GitHubRepoRequest request = linksParsingService.getGitHubRepoRequestByLink(link.getUrl().toString());

        GitHubRepoEventResponse response = gitHubClient.getLastRepositoryEvent(request).block();

        GitHubLinkInfo linkInfo = gitHubLinkInfoService.findLinkInfoByLinkUrl(link.getUrl());

        long responseLastEventId = response.getId();
        long savedLastEventId = linkInfo.getLastEventId();

        if (responseLastEventId == savedLastEventId) {
            return LinkUpdateInfo.updateInfoWithoutUpdate();
        }

        linkInfo.setLastEventId(responseLastEventId);
        gitHubLinkInfoService.updateLinkInfo(linkInfo.getId(), linkInfo);
        String message = "New event: %s".formatted(response.getType());
        return LinkUpdateInfo.updateInfoWithUpdate(message, link);
    }

}
