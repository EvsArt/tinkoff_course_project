package edu.java.service.jooq;

import edu.java.client.StackOverflowClient;
import edu.java.domain.jooqRepository.JooqStackOverFlowLinkInfoRepository;
import edu.java.dto.stackoverflow.StackOverflowQuestionRequest;
import edu.java.dto.stackoverflow.StackOverflowQuestionResponse;
import edu.java.model.entity.Link;
import edu.java.model.entity.StackOverFlowLinkInfo;
import edu.java.service.LinksParsingService;
import edu.java.service.StackOverFlowLinkInfoService;
import java.net.URI;

public class JooqStackOverFlowLinkInfoService implements StackOverFlowLinkInfoService {
    private final StackOverflowClient stackOverflowClient;
    private final LinksParsingService linksParsingService;
    private final JooqStackOverFlowLinkInfoRepository stackOverFlowLinkInfoRepository;

    public JooqStackOverFlowLinkInfoService(
        StackOverflowClient stackOverflowClient,
        LinksParsingService linksParsingService,
        JooqStackOverFlowLinkInfoRepository stackOverFlowLinkInfoRepository
    ) {
        this.stackOverflowClient = stackOverflowClient;
        this.linksParsingService = linksParsingService;
        this.stackOverFlowLinkInfoRepository = stackOverFlowLinkInfoRepository;
    }

    @Override
    public StackOverFlowLinkInfo findLinkInfoByLinkId(long linkId) {
        return stackOverFlowLinkInfoRepository.findLinkInfoByLinkId(linkId)
            .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public StackOverFlowLinkInfo addLinkInfo(Link link) {
        StackOverflowQuestionRequest request =
            linksParsingService.getStackOverFlowQuestionRequestByLink(link.getUrl().toString());
        StackOverflowQuestionResponse response = stackOverflowClient.getQuestion(request).block();
        StackOverFlowLinkInfo linkInfo = new StackOverFlowLinkInfo(link, response.answerCount());
        return stackOverFlowLinkInfoRepository.insertLinkInfo(linkInfo).get();
    }

    @Override
    public StackOverFlowLinkInfo updateLinkInfo(long linkId, StackOverFlowLinkInfo linkInfo) {
        StackOverFlowLinkInfo oldInfo = findLinkInfoByLinkId(linkId);
        linkInfo.setId(oldInfo.getId());
        return stackOverFlowLinkInfoRepository.updateLinkInfo(linkInfo).get();
    }

    @Override
    public StackOverFlowLinkInfo findLinkInfoByLinkUrl(URI url) {
        return stackOverFlowLinkInfoRepository.findLinkInfoByUrl(url).get();
    }

    @Override
    public StackOverFlowLinkInfo removeLinkInfoByLink(Link link) {
        StackOverFlowLinkInfo linkInfo = findLinkInfoByLinkUrl(link.getUrl());
        return stackOverFlowLinkInfoRepository.removeLinkInfoById(linkInfo.getId()).get();
    }
}
