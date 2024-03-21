package edu.java.service;

import edu.java.client.service.SupportedApi;
import edu.java.model.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkInfoService {

    private final GitHubLinkInfoService gitHubLinkInfoService;
    private final StackOverFlowLinkInfoService stackOverFlowLinkInfoService;

    @Autowired
    public LinkInfoService(
        GitHubLinkInfoService gitHubLinkInfoService,
        StackOverFlowLinkInfoService stackOverFlowLinkInfoService
    ) {
        this.gitHubLinkInfoService = gitHubLinkInfoService;
        this.stackOverFlowLinkInfoService = stackOverFlowLinkInfoService;
    }

    public long addLinkInfoByLink(Link link) {
        SupportedApi api = SupportedApi.getApiByLink(link.getUrl().toString());
        return switch (api) {
            case GITHUB_REPO -> gitHubLinkInfoService.addLinkInfo(link).getId();
            case STACKOVERFLOW_QUESTION -> stackOverFlowLinkInfoService.addLinkInfo(link).getId();
        };
    }

    public long removeLinkInfoByLink(Link link) {
        SupportedApi api = SupportedApi.getApiByLink(link.getUrl().toString());
        return switch (api) {
            case GITHUB_REPO -> gitHubLinkInfoService.removeLinkInfoByLink(link).getId();
            case STACKOVERFLOW_QUESTION -> stackOverFlowLinkInfoService.removeLinkInfoByLink(link).getId();
        };
    }
}
