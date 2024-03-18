package edu.java.repository.jdbc;

import edu.java.exceptions.LinkNotExistsException;
import edu.java.model.Link;
import edu.java.model.StackOverFlowLinkInfo;
import edu.java.repository.StackOverFlowLinkInfoRepository;
import edu.java.service.SqlQueries;
import java.net.URI;
import java.sql.Types;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Slf4j
public class JdbcStackOverFlowLinkInfoRepository implements StackOverFlowLinkInfoRepository {

    private final JdbcClient jdbcClient;

    private final String stackoverflowLinkInfoTableName = SqlQueries.STACKOVERFLOW_LINK_INFO_TABLE_NAME;
    private final List<String> stackoverflowLinkInfoFieldsNamesWithoutId =
        SqlQueries.STACKOVERFLOW_LINK_INFO_FIELDS_NAMES_WITHOUT_ID;

    public JdbcStackOverFlowLinkInfoRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    @Transactional
    public Optional<StackOverFlowLinkInfo> findLinkInfoByLinkId(long linkId) {
        log.debug("findLinkInfoByLinkId() was called with linkId={}", linkId);
        String sql =
            SqlQueries.findWhereQuery(stackoverflowLinkInfoTableName, SqlQueries.STACKOVERFLOW_LINK_INFO_FIELD_ID_NAME);
        return jdbcClient.sql(sql)
            .param(SqlQueries.STACKOVERFLOW_LINK_INFO_FIELD_ID_NAME, linkId, Types.BIGINT)
            .query(StackOverFlowLinkInfo.class)
            .optional();
    }

    @Override
    @Transactional
    public Optional<StackOverFlowLinkInfo> findLinkInfoById(long id) {
        log.debug("findLinkInfoById() was called with id={}", id);
        String sql =
            SqlQueries.findWhereQuery(stackoverflowLinkInfoTableName, SqlQueries.STACKOVERFLOW_LINK_INFO_FIELD_ID_NAME);
        return jdbcClient.sql(sql)
            .param(SqlQueries.STACKOVERFLOW_LINK_INFO_FIELD_ID_NAME, id, Types.BIGINT)
            .query(StackOverFlowLinkInfo.class)
            .optional();
    }

    @Override
    @Transactional
    public Optional<StackOverFlowLinkInfo> insertLinkInfo(StackOverFlowLinkInfo linkInfo) {
        log.debug("insertLinkInfo() was called with linkInfo={}", linkInfo);
        String sql = SqlQueries.insertQuery(stackoverflowLinkInfoTableName, stackoverflowLinkInfoFieldsNamesWithoutId);
        KeyHolder idHolder = new GeneratedKeyHolder();
        int updated = jdbcClient.sql(sql)
            .param(SqlQueries.STACKOVERFLOW_LINK_INFO_FIELD_LINK_ID_NAME, linkInfo.getLink().getId(), Types.BIGINT)
            .param(
                SqlQueries.STACKOVERFLOW_LINK_INFO_FIELD_EVENTS_COUNT_NAME,
                linkInfo.getAnswersCount(),
                Types.INTEGER
            )
            .update(idHolder, SqlQueries.STACKOVERFLOW_LINK_INFO_FIELD_ID_NAME);
        if (updated == 0) {
            return Optional.empty();
        }
        long id = idHolder.getKey().longValue();
        log.info("insertLinkInfo(): chat saved with id={}", id);
        return findLinkInfoById(id);
    }

    @Override
    @Transactional
    public Optional<StackOverFlowLinkInfo> updateLinkInfo(StackOverFlowLinkInfo linkInfo) {
        log.debug("updateLinkInfo() was called with id={}: linkInfo={}", linkInfo.getId(), linkInfo);
        String sql =
            SqlQueries.updateQuery(
                stackoverflowLinkInfoTableName,
                stackoverflowLinkInfoFieldsNamesWithoutId,
                SqlQueries.STACKOVERFLOW_LINK_INFO_FIELD_ID_NAME
            );
        int updated = jdbcClient.sql(sql)
            .param(SqlQueries.STACKOVERFLOW_LINK_INFO_FIELD_LINK_ID_NAME, linkInfo.getLink().getId(), Types.BIGINT)
            .param(
                SqlQueries.STACKOVERFLOW_LINK_INFO_FIELD_EVENTS_COUNT_NAME,
                linkInfo.getAnswersCount(),
                Types.INTEGER
            )
            .param(
                SqlQueries.STACKOVERFLOW_LINK_INFO_FIELD_ID_NAME,
                linkInfo.getId(),
                Types.BIGINT
            )  // id in searching condition
            .update();
        log.info("updateLinkInfo(): {} rows were updated", updated);
        return findLinkInfoById(linkInfo.getId());
    }

    @Override
    @Transactional
    public Optional<StackOverFlowLinkInfo> removeLinkInfoById(Long id) {
        log.debug("removeLinkInfoById() was called with id={}", id);
        String sql =
            SqlQueries.deleteQuery(stackoverflowLinkInfoTableName, SqlQueries.STACKOVERFLOW_LINK_INFO_FIELD_ID_NAME);
        Optional<StackOverFlowLinkInfo> oldLinkInfo = findLinkInfoById(id);
        if (oldLinkInfo.isEmpty()) {
            return oldLinkInfo;
        }
        int updated = jdbcClient.sql(sql)
            .param(SqlQueries.STACKOVERFLOW_LINK_INFO_FIELD_ID_NAME, id, Types.BIGINT)
            .update();
        log.debug("removeLinkInfoById(): {} rows were updated", updated);
        return oldLinkInfo;
    }

    @Override
    public Optional<StackOverFlowLinkInfo> findLinkInfoByUrl(URI url) {
        log.debug("findLinkInfoByUrl() was called with url={}", url);
        // getting link by url
        String findLinkSql = SqlQueries.findWhereQuery(SqlQueries.LINK_TABLE_NAME, SqlQueries.LINK_FIELD_URL_NAME);
        Link link = jdbcClient.sql(findLinkSql)
            .param(SqlQueries.LINK_FIELD_URL_NAME, url.toString(), Types.VARCHAR)
            .query(Link.class)
            .optional().orElseThrow(LinkNotExistsException::new);
        return findLinkInfoByLinkId(link.getId());
    }

}
