package edu.java.service;

import edu.java.model.Link;
import edu.java.model.StackOverFlowLinkInfo;
import java.net.URI;

public interface StackOverFlowLinkInfoService {
    StackOverFlowLinkInfo findLinkInfoByLinkId(long linkId);

    StackOverFlowLinkInfo findLinkInfoByLinkUrl(URI url);

    StackOverFlowLinkInfo addLinkInfo(Link link);

    StackOverFlowLinkInfo updateLinkInfo(long linkId, StackOverFlowLinkInfo linkInfo);

    StackOverFlowLinkInfo removeLinkInfoByLink(Link link);
}
