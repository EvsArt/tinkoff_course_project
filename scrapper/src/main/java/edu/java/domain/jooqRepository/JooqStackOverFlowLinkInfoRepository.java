package edu.java.domain.jooqRepository;

import edu.java.domain.StackOverFlowLinkInfoRepository;
import edu.java.model.StackOverFlowLinkInfo;
import java.net.URI;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.domain.jooq.Tables.GITHUB_LINK_INFO;
import static edu.java.domain.jooq.Tables.LINK;
import static edu.java.domain.jooq.Tables.STACKOVERFLOW_LINK_INFO;

@Slf4j
@Repository
public class JooqStackOverFlowLinkInfoRepository implements StackOverFlowLinkInfoRepository {

    private final DefaultDSLContext dsl;

    public JooqStackOverFlowLinkInfoRepository(DefaultDSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    @Transactional
    public Optional<StackOverFlowLinkInfo> findLinkInfoByLinkId(long linkId) {
        log.debug("findLinkInfoByLinkId() was called with linkId={}", linkId);
        return dsl.select()
            .from(STACKOVERFLOW_LINK_INFO)
            .where(STACKOVERFLOW_LINK_INFO.LINK_ID.eq(linkId))
            .fetchOptionalInto(StackOverFlowLinkInfo.class);
    }

    @Override
    @Transactional
    public Optional<StackOverFlowLinkInfo> findLinkInfoById(long id) {
        log.debug("findLinkInfoById() was called with id={}", id);
        return dsl.select()
            .from(STACKOVERFLOW_LINK_INFO)
            .where(STACKOVERFLOW_LINK_INFO.ID.eq(id))
            .fetchOptionalInto(StackOverFlowLinkInfo.class);
    }

    @Override
    @Transactional
    public Optional<StackOverFlowLinkInfo> insertLinkInfo(StackOverFlowLinkInfo linkInfo) {
        log.debug("insertLinkInfo() was called with linkInfo={}", linkInfo);
        int updated = dsl.insertInto(GITHUB_LINK_INFO)
            .set(STACKOVERFLOW_LINK_INFO.LINK_ID, linkInfo.getLink().getId())
            .set(STACKOVERFLOW_LINK_INFO.ANSWERS_COUNT, linkInfo.getAnswersCount())
            .execute();
        if (updated == 0) {
            return Optional.empty();
        }
        Optional<StackOverFlowLinkInfo> savedInfo = findLinkInfoByLinkId(linkInfo.getLink().getId());
        savedInfo.ifPresent((info) -> log.info("insertLinkInfo(): chat saved with id={}", info));
        return savedInfo;
    }

    @Override
    @Transactional
    public Optional<StackOverFlowLinkInfo> updateLinkInfo(StackOverFlowLinkInfo linkInfo) {
        log.debug("updateLinkInfo() was called with id={}: linkInfo={}", linkInfo.getId(), linkInfo);
        int updated = dsl
            .update(STACKOVERFLOW_LINK_INFO)
            .set(STACKOVERFLOW_LINK_INFO.LINK_ID, linkInfo.getLink().getId())
            .set(STACKOVERFLOW_LINK_INFO.ANSWERS_COUNT, linkInfo.getAnswersCount())
            .where(STACKOVERFLOW_LINK_INFO.ID.eq(linkInfo.getId()))
            .execute();
        log.info("updateLinkInfo(): {} rows were updated", updated);
        return findLinkInfoById(linkInfo.getId());
    }

    @Override
    @Transactional
    public Optional<StackOverFlowLinkInfo> removeLinkInfoById(Long id) {
        log.debug("removeLinkInfoById() was called with id={}", id);
        Optional<StackOverFlowLinkInfo> oldLinkInfo = findLinkInfoById(id);
        int updated = dsl.deleteFrom(STACKOVERFLOW_LINK_INFO)
            .where(STACKOVERFLOW_LINK_INFO.ID.eq(id))
            .execute();
        if (oldLinkInfo.isEmpty()) {
            return oldLinkInfo;
        }
        log.debug("removeLinkInfoById(): {} rows were updated", updated);
        return oldLinkInfo;
    }

    @Override
    public Optional<StackOverFlowLinkInfo> findLinkInfoByUrl(URI url) {
        log.debug("findLinkInfoByUrl() was called with url={}", url);
        // getting link by url
        return dsl.select()
            .from(LINK)
            .join(STACKOVERFLOW_LINK_INFO)
            .on(LINK.ID.eq(STACKOVERFLOW_LINK_INFO.LINK_ID))
            .where(LINK.URL.eq(url.toString()))
            .fetchOptionalInto(StackOverFlowLinkInfo.class);
    }

}
