package edu.java.service.jdbc;

import edu.java.servicesClient.GitHubClient;
import edu.java.domain.jdbcRepository.JdbcGitHubLinkInfoRepository;
import edu.java.domain.jdbcRepository.JdbcLinkRepository;
import edu.java.dto.github.GitHubRepoEventResponse;
import edu.java.dto.github.GitHubRepoRequest;
import edu.java.exceptions.LinkNotExistsException;
import edu.java.model.entity.GitHubLinkInfo;
import edu.java.model.entity.Link;
import edu.java.service.GitHubLinkInfoService;
import edu.java.service.LinksParsingService;
import java.net.URI;

public class JdbcGitHubLinkInfoService implements GitHubLinkInfoService {

    private final GitHubClient gitHubClient;
    private final LinksParsingService linksParsingService;
    private final JdbcGitHubLinkInfoRepository gitHubLinkInfoRepository;
    private final JdbcLinkRepository linkRepository;

    public JdbcGitHubLinkInfoService(
        GitHubClient gitHubClient,
        LinksParsingService linksParsingService,
        JdbcGitHubLinkInfoRepository gitHubLinkInfoRepository,
        JdbcLinkRepository linkRepository
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
