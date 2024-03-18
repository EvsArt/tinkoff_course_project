package edu.java.repository;

import edu.java.model.GitHubLinkInfo;
import edu.java.model.StackOverFlowLinkInfo;
import java.util.Optional;

public interface StackOverFlowLinkInfoRepository {

    Optional<StackOverFlowLinkInfo> findLinkInfoByLinkId(long linkId);
    Optional<StackOverFlowLinkInfo> insertLinkInfo(StackOverFlowLinkInfo linkInfo);
    Optional<StackOverFlowLinkInfo> updateLinkInfo(StackOverFlowLinkInfo linkInfo);
    Optional<StackOverFlowLinkInfo> removeLinkInfo(StackOverFlowLinkInfo linkInfo);

}
