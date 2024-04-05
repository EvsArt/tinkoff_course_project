package edu.java.service.jpa;

import edu.java.client.StackOverflowClient;
import edu.java.domain.jpaRepository.JpaStackOverFlowLinkInfoRepository;
import edu.java.dto.StackOverflowQuestionRequest;
import edu.java.exceptions.LinkNotExistsException;
import edu.java.model.entity.Link;
import edu.java.model.entity.StackOverFlowLinkInfo;
import edu.java.service.LinksParsingService;
import edu.java.service.StackOverFlowLinkInfoService;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.SerializationUtils;

@Slf4j
public class JpaStackOverFlowLinkInfoService implements StackOverFlowLinkInfoService {
    private final JpaStackOverFlowLinkInfoRepository linkInfoRepository;
    private final StackOverflowClient client;
    private final LinksParsingService linksParsingService;

    public JpaStackOverFlowLinkInfoService(
        JpaStackOverFlowLinkInfoRepository linkInfoRepository,
        StackOverflowClient client,
        LinksParsingService linksParsingService
    ) {
        this.linkInfoRepository = linkInfoRepository;
        this.client = client;
        this.linksParsingService = linksParsingService;
    }

    @Override
    @Transactional
    public StackOverFlowLinkInfo findLinkInfoByLinkId(long linkId) {
        log.debug("findLinkInfoByLinkId() was called with linkId={}", linkId);
        return linkInfoRepository.findByLinkId(linkId).orElseThrow(LinkNotExistsException::new);
    }

    @Override
    @Transactional
    public StackOverFlowLinkInfo findLinkInfoByLinkUrl(URI url) {
        log.debug("findLinkInfoByLinkUrl() was called with url={}", url);
        return linkInfoRepository.findByLinkUrl(url).orElseThrow(LinkNotExistsException::new);
    }

    @Override
    @Transactional
    public StackOverFlowLinkInfo addLinkInfo(Link link) {
        log.debug("addLinkInfo() was called with link={}", link);
        StackOverflowQuestionRequest request =
            linksParsingService.getStackOverFlowQuestionRequestByLink(link.getUrl().toString());
        int answerCount = client.getQuestion(request).block().answerCount();

        StackOverFlowLinkInfo linkInfo = linkInfoRepository.findByLinkUrl(link.getUrl())
            .orElse(new StackOverFlowLinkInfo(link, answerCount));
        return linkInfoRepository.save(linkInfo);
    }

    @Override
    @Transactional
    public StackOverFlowLinkInfo updateLinkInfo(long linkId, StackOverFlowLinkInfo linkInfo) {
        log.debug("updateLinkInfo() was called with linkId={}, linkInfo={}", linkId, linkInfo);
        StackOverFlowLinkInfo newLinkInfo = new StackOverFlowLinkInfo(linkInfo);
        newLinkInfo.setId(linkId);
        linkInfoRepository.save(newLinkInfo);
        return newLinkInfo;
    }

    @Override
    @Transactional
    public StackOverFlowLinkInfo removeLinkInfoByLink(Link link) {
        log.debug("removeLinkInfoByLink() was called with link={}", link);
        StackOverFlowLinkInfo oldLinkInfo =
            linkInfoRepository.findByLinkUrl(link.getUrl()).orElseThrow(LinkNotExistsException::new);
        linkInfoRepository.deleteByLink(link);
        return oldLinkInfo;
    }
}
