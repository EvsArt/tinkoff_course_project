package edu.java.domain;

import edu.java.model.StackOverFlowLinkInfo;
import java.net.URI;
import java.util.Optional;

public interface StackOverFlowLinkInfoRepository {

    Optional<StackOverFlowLinkInfo> findLinkInfoByLinkId(long linkId);

    Optional<StackOverFlowLinkInfo> findLinkInfoById(long id);

    Optional<StackOverFlowLinkInfo> insertLinkInfo(StackOverFlowLinkInfo linkInfo);

    Optional<StackOverFlowLinkInfo> updateLinkInfo(StackOverFlowLinkInfo linkInfo);

    Optional<StackOverFlowLinkInfo> removeLinkInfoById(Long id);

    Optional<StackOverFlowLinkInfo> findLinkInfoByUrl(URI url);

}
