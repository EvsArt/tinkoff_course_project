package edu.java.service.jpa;

import edu.java.client.GitHubClient;
import edu.java.domain.jpaRepository.JpaGitHubLinkInfoRepository;
import edu.java.dto.GitHubRepoRequest;
import edu.java.exceptions.LinkNotExistsException;
import edu.java.model.entity.GitHubLinkInfo;
import edu.java.model.entity.Link;
import edu.java.service.GitHubLinkInfoService;
import edu.java.service.LinksParsingService;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.SerializationUtils;

@Slf4j
public class JpaGitHubLinkInfoService implements GitHubLinkInfoService {

    private final JpaGitHubLinkInfoRepository linkInfoRepository;
    private final GitHubClient client;
    private final LinksParsingService linksParsingService;

    public JpaGitHubLinkInfoService(
        JpaGitHubLinkInfoRepository linkInfoRepository, GitHubClient client,
        LinksParsingService linksParsingService
    ) {
        this.linkInfoRepository = linkInfoRepository;
        this.client = client;
        this.linksParsingService = linksParsingService;
    }

    @Override
    @Transactional
    public GitHubLinkInfo findLinkInfoByLinkId(long linkId) {
        log.debug("findLinkInfoByLinkId() was called with linkId={}", linkId);
        return linkInfoRepository.findByLinkId(linkId).orElseThrow(LinkNotExistsException::new);
    }

    @Override
    @Transactional
    public GitHubLinkInfo findLinkInfoByLinkUrl(URI url) {
        log.debug("findLinkInfoByLinkUrl() was called with url={}", url);
        return linkInfoRepository.findByLinkUrl(url).orElseThrow(LinkNotExistsException::new);
    }

    @Override
    @Transactional
    public GitHubLinkInfo addLinkInfo(Link link) {
        log.debug("addLinkInfo() was called with link={}", link);
        GitHubRepoRequest request = linksParsingService.getGitHubRepoRequestByLink(link.getUrl().toString());
        long lastEventId = client.getLastRepositoryEvent(request).block().getId();

        GitHubLinkInfo linkInfo = linkInfoRepository.findByLinkUrl(link.getUrl())
            .orElse(new GitHubLinkInfo(link, lastEventId));
        return linkInfoRepository.save(linkInfo);
    }

    @Override
    @Transactional
    public GitHubLinkInfo updateLinkInfo(long linkId, GitHubLinkInfo linkInfo) {
        log.debug("updateLinkInfo() was called with linkId={}, linkInfo={}", linkId, linkInfo);
        GitHubLinkInfo newLinkInfo = new GitHubLinkInfo(linkInfo);
        newLinkInfo.setId(linkId);
        linkInfoRepository.save(newLinkInfo);
        return newLinkInfo;
    }

    @Override
    @Transactional
    public GitHubLinkInfo removeLinkInfoByLink(Link link) {
        log.debug("removeLinkInfoByLink() was called with link={}", link);
        GitHubLinkInfo oldLinkInfo =
            linkInfoRepository.findByLinkUrl(link.getUrl()).orElseThrow(LinkNotExistsException::new);
        linkInfoRepository.deleteByLink(link);
        return oldLinkInfo;
    }
}
