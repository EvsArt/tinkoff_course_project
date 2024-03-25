package edu.java.domain.jooqRepository;

import edu.java.domain.GitHubLinkInfoRepository;
import edu.java.model.entity.GitHubLinkInfo;
import java.net.URI;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.domain.jooq.Tables.GITHUB_LINK_INFO;
import static edu.java.domain.jooq.Tables.LINK;

@Slf4j
@Repository
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqGitHubLinkInfoRepository implements GitHubLinkInfoRepository {

    private final DefaultDSLContext dsl;

    public JooqGitHubLinkInfoRepository(DefaultDSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    @Transactional
    public Optional<GitHubLinkInfo> findLinkInfoByLinkId(long linkId) {
        log.debug("findLinkInfoByLinkId() was called with linkId={}", linkId);
        return dsl.select()
            .from(GITHUB_LINK_INFO)
            .where(GITHUB_LINK_INFO.LINK_ID.eq(linkId))
            .fetchOptionalInto(GitHubLinkInfo.class);
    }

    @Override
    @Transactional
    public Optional<GitHubLinkInfo> findLinkInfoById(long id) {
        log.debug("findLinkInfoById() was called with id={}", id);
        return dsl.select()
            .from(GITHUB_LINK_INFO)
            .where(GITHUB_LINK_INFO.ID.eq(id))
            .fetchOptionalInto(GitHubLinkInfo.class);
    }

    @Override
    @Transactional
    public Optional<GitHubLinkInfo> insertLinkInfo(GitHubLinkInfo linkInfo) {
        log.debug("insertLinkInfo() was called with linkInfo={}", linkInfo);
        int updated = dsl.insertInto(GITHUB_LINK_INFO)
            .set(GITHUB_LINK_INFO.LINK_ID, linkInfo.getLink().getId())
            .set(GITHUB_LINK_INFO.LAST_EVENT_ID, linkInfo.getLastEventId())
            .execute();
        if (updated == 0) {
            return Optional.empty();
        }
        Optional<GitHubLinkInfo> savedInfo = findLinkInfoByLinkId(linkInfo.getLink().getId());
        savedInfo.ifPresent((info) -> log.info("insertLinkInfo(): chat saved with id={}", info));
        return savedInfo;
    }

    @Override
    @Transactional
    public Optional<GitHubLinkInfo> updateLinkInfo(GitHubLinkInfo linkInfo) {
        log.debug("updateLinkInfo() was called with id={}: linkInfo={}", linkInfo.getId(), linkInfo);
        int updated = dsl
            .update(GITHUB_LINK_INFO)
            .set(GITHUB_LINK_INFO.LINK_ID, linkInfo.getLink().getId())
            .set(GITHUB_LINK_INFO.LAST_EVENT_ID, linkInfo.getLastEventId())
            .where(GITHUB_LINK_INFO.ID.eq(linkInfo.getId()))
            .execute();
        log.info("updateLinkInfo(): {} rows were updated", updated);
        return findLinkInfoById(linkInfo.getId());
    }

    @Override
    @Transactional
    public Optional<GitHubLinkInfo> removeLinkInfoById(Long id) {
        log.debug("removeLinkInfoById() was called with id={}", id);
        Optional<GitHubLinkInfo> oldLinkInfo = findLinkInfoById(id);
        int updated = dsl.deleteFrom(GITHUB_LINK_INFO)
            .where(GITHUB_LINK_INFO.ID.eq(id))
            .execute();
        if (oldLinkInfo.isEmpty()) {
            return oldLinkInfo;
        }
        log.debug("removeLinkInfoById(): {} rows were updated", updated);
        return oldLinkInfo;
    }

    @Override
    public Optional<GitHubLinkInfo> findLinkInfoByUrl(URI url) {
        log.debug("findLinkInfoByUrl() was called with url={}", url);
        // getting link by url
        return dsl.select()
            .from(LINK)
            .join(GITHUB_LINK_INFO)
            .on(LINK.ID.eq(GITHUB_LINK_INFO.LINK_ID))
            .where(LINK.URL.eq(url.toString()))
            .fetchOptionalInto(GitHubLinkInfo.class);
    }
}
