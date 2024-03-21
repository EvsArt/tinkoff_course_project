package edu.java.domain;

import edu.java.model.GitHubLinkInfo;
import java.net.URI;
import java.util.Optional;

public interface GitHubLinkInfoRepository {

    Optional<GitHubLinkInfo> findLinkInfoByLinkId(long linkId);

    Optional<GitHubLinkInfo> findLinkInfoById(long id);

    Optional<GitHubLinkInfo> insertLinkInfo(GitHubLinkInfo linkInfo);

    Optional<GitHubLinkInfo> updateLinkInfo(GitHubLinkInfo linkInfo);

    Optional<GitHubLinkInfo> removeLinkInfoById(Long id);

    Optional<GitHubLinkInfo> findLinkInfoByUrl(URI url);
}
