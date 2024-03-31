package edu.java.domain.jpaRepository;

import edu.java.model.entity.GitHubLinkInfo;
import edu.java.model.entity.Link;
import java.net.URI;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public interface JpaGitHubLinkInfoRepository extends JpaRepository<GitHubLinkInfo, Long> {
    Optional<GitHubLinkInfo> findByLinkId(long linkId);

    Optional<GitHubLinkInfo> findByLinkUrl(URI url);

    int deleteByLink(Link link);
}
