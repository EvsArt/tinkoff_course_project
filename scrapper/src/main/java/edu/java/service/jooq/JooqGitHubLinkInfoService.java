package edu.java.service.jooq;

import edu.java.client.GitHubClient;
import edu.java.domain.jooqRepository.JooqGitHubLinkInfoRepository;
import edu.java.domain.jooqRepository.JooqLinkRepository;
import edu.java.dto.GitHubRepoEventResponse;
import edu.java.dto.GitHubRepoRequest;
import edu.java.exceptions.LinkNotExistsException;
import edu.java.model.entity.GitHubLinkInfo;
import edu.java.model.entity.Link;
import edu.java.service.GitHubLinkInfoService;
import edu.java.service.LinksParsingService;
import java.net.URI;

public class JooqGitHubLinkInfoService implements GitHubLinkInfoService {

    private final GitHubClient gitHubClient;
    private final LinksParsingService linksParsingService;
    private final JooqGitHubLinkInfoRepository gitHubLinkInfoRepository;
    private final JooqLinkRepository linkRepository;

    public JooqGitHubLinkInfoService(
        GitHubClient gitHubClient,
        LinksParsingService linksParsingService,
        JooqGitHubLinkInfoRepository gitHubLinkInfoRepository,
        JooqLinkRepository linkRepository
    ) {
        this.gitHubClient = gitHubClient;
        this.linksParsingService = linksParsingService;
        this.gitHubLinkInfoRepository = gitHubLinkInfoRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    public GitHubLinkInfo findLinkInfoByLinkId(long linkId) {
        return gitHubLinkInfoRepository.findLinkInfoByLinkId(linkId)
            .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public GitHubLinkInfo addLinkInfo(Link link) {
        GitHubRepoRequest request = linksParsingService.getGitHubRepoRequestByLink(link.getUrl().toString());
        GitHubRepoEventResponse response = gitHubClient.getLastRepositoryEvent(request).block();
        GitHubLinkInfo linkInfo = new GitHubLinkInfo(link, response.getId());
        return gitHubLinkInfoRepository.insertLinkInfo(linkInfo).get();
    }

    @Override
    public GitHubLinkInfo updateLinkInfo(long linkId, GitHubLinkInfo linkInfo) {
        GitHubLinkInfo oldInfo = findLinkInfoByLinkId(linkId);
        linkInfo.setId(oldInfo.getId());
        linkInfo.setLink(linkRepository.findLinkById(linkId).get());
        return gitHubLinkInfoRepository.updateLinkInfo(linkInfo).get();
    }

    @Override
    public GitHubLinkInfo findLinkInfoByLinkUrl(URI url) {
        return gitHubLinkInfoRepository.findLinkInfoByUrl(url).orElseThrow(LinkNotExistsException::new);
    }

    @Override
    public GitHubLinkInfo removeLinkInfoByLink(Link link) {
        GitHubLinkInfo linkInfo = findLinkInfoByLinkUrl(link.getUrl());
        return gitHubLinkInfoRepository.removeLinkInfoById(linkInfo.getId()).orElseThrow(LinkNotExistsException::new);
    }
}
