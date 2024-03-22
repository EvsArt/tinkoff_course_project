package edu.java.domain.jpaRepository;

import edu.java.model.entity.GitHubLinkInfo;
import edu.java.model.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.net.URI;
import java.util.Optional;

@Repository
public interface JpaGitHubLinkInfoRepository extends JpaRepository<GitHubLinkInfo, Long> {
    Optional<GitHubLinkInfo> findByLinkId(long linkId);

    Optional<GitHubLinkInfo> findByLinkUrl(URI url);

    int deleteByLink(Link link);
}
