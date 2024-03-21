package edu.java.service;

import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import edu.java.client.service.SupportedApi;
import edu.java.constants.StringConstants;
import edu.java.dto.GitHubRepoRequest;
import edu.java.dto.GitHubRepoResponse;
import edu.java.dto.StackOverflowQuestionRequest;
import edu.java.dto.StackOverflowQuestionResponse;
import edu.java.model.Link;
import edu.java.model.LinkUpdateInfo;
import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkUpdaterServiceImpl implements LinkUpdaterService {

    private final LinksParsingServiceImpl linksParsingService;
    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;
    private final LinkService linkService;

    @Autowired
    public LinkUpdaterServiceImpl(
        GitHubClient gitHubClient,
        StackOverflowClient stackOverflowClient,
        LinksParsingServiceImpl linksParsingService,
        LinkService linkService
    ) {
        this.gitHubClient = gitHubClient;
        this.stackOverflowClient = stackOverflowClient;
        this.linksParsingService = linksParsingService;
        this.linkService = linkService;
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
        GitHubRepoRequest request = linksParsingService.getGitHubRepoRequestByLink(link.getUrl().toString());

        GitHubRepoResponse response = gitHubClient.getRepositoryByOwnerNameAndRepoName(request).block();

        OffsetDateTime resourcePushedAt = response.pushedAt();
        OffsetDateTime savedUpdatedAt = link.getLastUpdateTime();

        if (resourcePushedAt.isAfter(savedUpdatedAt)) {
            linkService.setLastUpdateTime(link.getId(), resourcePushedAt);
            return LinkUpdateInfo.updateInfoWithUpdate(StringConstants.REPOSITORY_WAS_UPDATED, link);
        }
        return LinkUpdateInfo.updateInfoWithoutUpdate();
    }

    public LinkUpdateInfo checkStackOverFlowQuestionUpdates(Link link) {
        StackOverflowQuestionRequest request = linksParsingService.getQuestionRequestByLink(link.getUrl().toString());

        StackOverflowQuestionResponse response = stackOverflowClient.getQuestionById(request).block();

        OffsetDateTime resourceUpdatedAt = response.lastActivityDate();
        OffsetDateTime savedUpdatedAt = link.getLastUpdateTime();

        if (resourceUpdatedAt.isAfter(savedUpdatedAt)) {
            linkService.setLastUpdateTime(link.getId(), resourceUpdatedAt);
            return LinkUpdateInfo.updateInfoWithUpdate(StringConstants.QUESTION_WAS_UPDATED, link);
        }
        return LinkUpdateInfo.updateInfoWithoutUpdate();
    }

}
