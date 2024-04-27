package edu.java.service.jpa;

import edu.java.domain.jpaRepository.JpaGitHubLinkInfoRepository;
import edu.java.domain.jpaRepository.JpaLinkRepository;
import edu.java.dto.github.GitHubRepoRequest;
import edu.java.exceptions.LinkNotExistsException;
import edu.java.exceptions.status.ResourceNotFoundException;
import edu.java.exceptions.status.StatusException;
import edu.java.model.entity.GitHubLinkInfo;
import edu.java.model.entity.Link;
import edu.java.service.GitHubLinkInfoService;
import edu.java.service.LinksParsingService;
import edu.java.servicesClient.GitHubClient;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
public class JpaGitHubLinkInfoService implements GitHubLinkInfoService {

    private final JpaGitHubLinkInfoRepository linkInfoRepository;
    private final JpaLinkRepository linkRepository;
    private final GitHubClient client;
    private final LinksParsingService linksParsingService;

    public JpaGitHubLinkInfoService(
        JpaGitHubLinkInfoRepository linkInfoRepository, JpaLinkRepository linkRepository, GitHubClient client,
        LinksParsingService linksParsingService
    ) {
        this.linkInfoRepository = linkInfoRepository;
        this.linkRepository = linkRepository;
        this.client = client;
        this.linksParsingService = linksParsingService;
    }

    @Override
    public GitHubLinkInfo findLinkInfoByLinkId(long linkId) {
        log.debug("findLinkInfoByLinkId() was called with linkId={}", linkId);
        return linkInfoRepository.findByLinkId(linkId).orElseThrow(LinkNotExistsException::new);
    }

    @Override
    public GitHubLinkInfo findLinkInfoByLinkUrl(URI url) {
        log.debug("findLinkInfoByLinkUrl() was called with url={}", url);
        return linkInfoRepository.findByLinkUrl(url).orElseThrow(LinkNotExistsException::new);
    }

    @Override
    public GitHubLinkInfo addLinkInfo(Link link) {
        log.debug("addLinkInfo() was called with link={}", link);
        GitHubRepoRequest request = linksParsingService.getGitHubRepoRequestByLink(link.getUrl().toString());
        long lastEventId;
        try {
            lastEventId = client.getLastRepositoryEvent(request).block().getId();
        } catch (StatusException e) {
            linkRepository.delete(link);
            throw new ResourceNotFoundException();
        }

        GitHubLinkInfo linkInfo = linkInfoRepository.findByLinkUrl(link.getUrl())
            .orElse(new GitHubLinkInfo(link, lastEventId));
        return linkInfoRepository.save(linkInfo);
    }

    @Override
    public GitHubLinkInfo updateLinkInfo(long linkId, GitHubLinkInfo linkInfo) {
        log.debug("updateLinkInfo() was called with linkId={}, linkInfo={}", linkId, linkInfo);
        GitHubLinkInfo newLinkInfo = new GitHubLinkInfo(linkInfo);
        newLinkInfo.setId(linkId);
        linkInfoRepository.save(newLinkInfo);
        return newLinkInfo;
    }

    @Override
    public GitHubLinkInfo removeLinkInfoByLink(Link link) {
        log.debug("removeLinkInfoByLink() was called with link={}", link);
        GitHubLinkInfo oldLinkInfo =
            linkInfoRepository.findByLinkUrl(link.getUrl()).orElseThrow(LinkNotExistsException::new);
        linkInfoRepository.deleteByLink(link);
        return oldLinkInfo;
    }
}
