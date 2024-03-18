package edu.java.service;

import edu.java.client.GitHubClient;
import edu.java.dto.GitHubRepoEventsResponse;
import edu.java.dto.GitHubRepoRequest;
import edu.java.model.GitHubLinkInfo;
import edu.java.model.Link;
import edu.java.repository.GitHubLinkInfoRepository;
import java.net.URI;
import org.springframework.stereotype.Service;

@Service
public class IGitHubLinkInfoService implements GitHubLinkInfoService {

    private final GitHubClient gitHubClient;
    private final LinksParsingService linksParsingService;
    private final GitHubLinkInfoRepository gitHubLinkInfoRepository;

    public IGitHubLinkInfoService(
        GitHubClient gitHubClient,
        LinksParsingService linksParsingService,
        GitHubLinkInfoRepository gitHubLinkInfoRepository
    ) {
        this.gitHubClient = gitHubClient;
        this.linksParsingService = linksParsingService;
        this.gitHubLinkInfoRepository = gitHubLinkInfoRepository;
    }

    @Override
    public GitHubLinkInfo findLinkInfoByLinkId(long linkId) {
        return gitHubLinkInfoRepository.findLinkInfoByLinkId(linkId)
            .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public GitHubLinkInfo addLinkInfo(Link link) {
        GitHubRepoRequest request = linksParsingService.getGitHubRepoRequestByLink(link.getUrl().toString());
        GitHubRepoEventsResponse response = gitHubClient.getRepositoryEvents(request).block();
        GitHubLinkInfo linkInfo = new GitHubLinkInfo(link, response.getEvents().size());
        return gitHubLinkInfoRepository.insertLinkInfo(linkInfo).get();
    }

    @Override
    public GitHubLinkInfo updateLinkInfo(long linkId, GitHubLinkInfo linkInfo) {
        GitHubLinkInfo oldInfo = findLinkInfoByLinkId(linkId);
        linkInfo.setId(oldInfo.getId());
        return gitHubLinkInfoRepository.updateLinkInfo(linkInfo).get();
    }

    @Override
    public GitHubLinkInfo findLinkInfoByLinkUrl(URI url) {
        return gitHubLinkInfoRepository.findLinkInfoByUrl(url).get();
    }

    @Override
    public GitHubLinkInfo removeLinkInfoByLink(Link link) {
        GitHubLinkInfo linkInfo = findLinkInfoByLinkUrl(link.getUrl());
        return gitHubLinkInfoRepository.removeLinkInfoById(linkInfo.getId()).get();
    }
}
