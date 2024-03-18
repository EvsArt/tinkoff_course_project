package edu.java.repository;

import edu.java.model.GitHubLinkInfo;
import java.util.Optional;

public interface GitHubLinkInfoRepository {

    Optional<GitHubLinkInfo> findLinkInfoByLinkId(long linkId);
    Optional<GitHubLinkInfo> insertLinkInfo(GitHubLinkInfo linkInfo);
    Optional<GitHubLinkInfo> updateLinkInfo(GitHubLinkInfo linkInfo);
    Optional<GitHubLinkInfo> removeLinkInfo(GitHubLinkInfo linkInfo);

}
