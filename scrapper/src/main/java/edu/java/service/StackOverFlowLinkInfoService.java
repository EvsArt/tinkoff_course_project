package edu.java.service;

import edu.java.model.StackOverFlowLinkInfo;
import java.net.URI;

public interface StackOverFlowLinkInfoService {
    StackOverFlowLinkInfo findLinkInfoByLinkId(long linkId);

    StackOverFlowLinkInfo addLinkInfo(StackOverFlowLinkInfo linkInfo);

    StackOverFlowLinkInfo updateLinkInfo(long linkId, StackOverFlowLinkInfo linkInfo);

    StackOverFlowLinkInfo findLinkInfoByLinkUrl(URI url);
}
