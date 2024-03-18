package edu.java.service;

import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import edu.java.client.service.SupportedApi;
import edu.java.dto.GitHubRepoEventResponse;
import edu.java.dto.GitHubRepoEventsResponse;
import edu.java.dto.GitHubRepoRequest;
import edu.java.dto.StackOverflowQuestionRequest;
import edu.java.dto.StackOverflowQuestionResponse;
import edu.java.model.GitHubLinkInfo;
import edu.java.model.Link;
import edu.java.model.LinkUpdateInfo;
import edu.java.model.StackOverFlowLinkInfo;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ILinkUpdaterService implements LinkUpdaterService {

    private final ILinksParsingService linksParsingService;
    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;
    private final LinkService linkService;
    private final GitHubLinkInfoService gitHubLinkInfoService;
    private final StackOverFlowLinkInfoService stackOverFlowLinkInfoService;

    @Autowired
    public ILinkUpdaterService(
        GitHubClient gitHubClient,
        StackOverflowClient stackOverflowClient,
        ILinksParsingService linksParsingService,
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
        return switch (api) {
            case GITHUB_REPO -> checkGitHubRepoUpdates(link);
            case STACKOVERFLOW_QUESTION -> checkStackOverFlowQuestionUpdates(link);
        };
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
            stackOverFlowLinkInfoService.updateLinkInfo(link.getId(), linkInfo);
            return LinkUpdateInfo.updateInfoWithoutUpdate();
        }
        return LinkUpdateInfo.updateInfoWithUpdate("There is a new answer!", link);
    }

    private LinkUpdateInfo checkNewGitHubEvents(Link link) {
        GitHubRepoRequest request = linksParsingService.getGitHubRepoRequestByLink(link.getUrl().toString());

        GitHubRepoEventsResponse response = gitHubClient.getRepositoryEvents(request).block();

        GitHubLinkInfo linkInfo = gitHubLinkInfoService.findLinkInfoByLinkUrl(link.getUrl());

        int responseEventsCount = response.getEvents().size();
        int savedEventsCount = linkInfo.getEventsCount();

        List<String> newEventsTypes = response.getEvents().stream()
            .limit(savedEventsCount - responseEventsCount)
            .map(GitHubRepoEventResponse::getType)
            .toList();

        if (newEventsTypes.isEmpty()) {
            return LinkUpdateInfo.updateInfoWithoutUpdate();
        }

        linkInfo.setEventsCount(responseEventsCount);
        gitHubLinkInfoService.updateLinkInfo(link.getId(), linkInfo);
        String message = "New events: %s".formatted(newEventsTypes);
        return LinkUpdateInfo.updateInfoWithUpdate(message, link);
    }

}
