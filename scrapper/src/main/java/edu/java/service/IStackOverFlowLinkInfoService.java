package edu.java.service;

import edu.java.client.StackOverflowClient;
import edu.java.dto.StackOverflowQuestionRequest;
import edu.java.dto.StackOverflowQuestionResponse;
import edu.java.model.Link;
import edu.java.model.StackOverFlowLinkInfo;
import edu.java.repository.StackOverFlowLinkInfoRepository;
import java.net.URI;
import org.springframework.stereotype.Service;

@Service
public class IStackOverFlowLinkInfoService implements StackOverFlowLinkInfoService {
    private final StackOverflowClient stackOverflowClient;
    private final LinksParsingService linksParsingService;
    private final StackOverFlowLinkInfoRepository stackOverFlowLinkInfoRepository;

    public IStackOverFlowLinkInfoService(
        StackOverflowClient stackOverflowClient,
        LinksParsingService linksParsingService,
        StackOverFlowLinkInfoRepository stackOverFlowLinkInfoRepository
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
