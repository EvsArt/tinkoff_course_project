package edu.java.domain.jdbcRepository;

import edu.java.domain.GitHubLinkInfoRepository;
import edu.java.exceptions.LinkNotExistsException;
import edu.java.model.entity.GitHubLinkInfo;
import edu.java.model.entity.Link;
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

@Slf4j
@Repository
public class JdbcGitHubLinkInfoRepository implements GitHubLinkInfoRepository {

    private final JdbcClient jdbcClient;

    private final String gitHubLinkInfoTableName = SqlQueries.GITHUB_LINK_INFO_TABLE_NAME;
    private final List<String> gitHubLinkInfoFieldsNamesWithoutId = SqlQueries.GITHUB_LINK_INFO_FIELDS_NAMES_WITHOUT_ID;

    public JdbcGitHubLinkInfoRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    @Transactional
    public Optional<GitHubLinkInfo> findLinkInfoByLinkId(long linkId) {
        log.debug("findLinkInfoByLinkId() was called with linkId={}", linkId);
        String sql = SqlQueries.findWhereQuery(gitHubLinkInfoTableName, SqlQueries.GITHUB_LINK_INFO_FIELD_LINK_ID_NAME);
        return jdbcClient.sql(sql)
            .param(SqlQueries.GITHUB_LINK_INFO_FIELD_LINK_ID_NAME, linkId, Types.BIGINT)
            .query(GitHubLinkInfo.class)
            .optional();
    }

    @Override
    @Transactional
    public Optional<GitHubLinkInfo> findLinkInfoById(long id) {
        log.debug("findLinkInfoById() was called with id={}", id);
        String sql = SqlQueries.findWhereQuery(gitHubLinkInfoTableName, SqlQueries.GITHUB_LINK_INFO_FIELD_ID_NAME);
        return jdbcClient.sql(sql)
            .param(SqlQueries.GITHUB_LINK_INFO_FIELD_ID_NAME, id, Types.BIGINT)
            .query(GitHubLinkInfo.class)
            .optional();
    }

    @Override
    @Transactional
    public Optional<GitHubLinkInfo> insertLinkInfo(GitHubLinkInfo linkInfo) {
        log.debug("insertLinkInfo() was called with linkInfo={}", linkInfo);
        String sql = SqlQueries.insertQuery(gitHubLinkInfoTableName, gitHubLinkInfoFieldsNamesWithoutId);
        KeyHolder idHolder = new GeneratedKeyHolder();
        int updated = jdbcClient.sql(sql)
            .param(SqlQueries.GITHUB_LINK_INFO_FIELD_LINK_ID_NAME, linkInfo.getLink().getId(), Types.BIGINT)
            .param(SqlQueries.GITHUB_LINK_INFO_FIELD_LAST_EVENT_ID_NAME, linkInfo.getLastEventId(), Types.INTEGER)
            .update(idHolder, SqlQueries.GITHUB_LINK_INFO_FIELD_LINK_ID_NAME);
        if (updated == 0) {
            return Optional.empty();
        }
        long id = idHolder.getKey().longValue();
        log.info("insertLinkInfo(): chat saved with id={}", id);
        return findLinkInfoById(id);
    }

    @Override
    @Transactional
    public Optional<GitHubLinkInfo> updateLinkInfo(GitHubLinkInfo linkInfo) {
        log.debug("updateLinkInfo() was called with id={}: linkInfo={}", linkInfo.getId(), linkInfo);
        String sql =
            SqlQueries.updateQuery(
                gitHubLinkInfoTableName,
                gitHubLinkInfoFieldsNamesWithoutId,
                SqlQueries.GITHUB_LINK_INFO_FIELD_ID_NAME
            );
        int updated = jdbcClient.sql(sql)
            .param(SqlQueries.GITHUB_LINK_INFO_FIELD_LINK_ID_NAME, linkInfo.getLink().getId(), Types.BIGINT)
            .param(SqlQueries.GITHUB_LINK_INFO_FIELD_LAST_EVENT_ID_NAME, linkInfo.getLastEventId(), Types.BIGINT)
            .param(
                SqlQueries.GITHUB_LINK_INFO_FIELD_ID_NAME,
                linkInfo.getId(),
                Types.BIGINT
            )  // id in searching condition
            .update();
        log.info("updateLinkInfo(): {} rows were updated", updated);
        return findLinkInfoById(linkInfo.getId());
    }

    @Override
    @Transactional
    public Optional<GitHubLinkInfo> removeLinkInfoById(Long id) {
        log.debug("removeLinkInfoById() was called with id={}", id);
        String sql = SqlQueries.deleteQuery(gitHubLinkInfoTableName, SqlQueries.GITHUB_LINK_INFO_FIELD_LINK_ID_NAME);
        Optional<GitHubLinkInfo> oldLinkInfo = findLinkInfoById(id);
        if (oldLinkInfo.isEmpty()) {
            return oldLinkInfo;
        }
        int updated = jdbcClient.sql(sql)
            .param(SqlQueries.GITHUB_LINK_INFO_FIELD_ID_NAME, id, Types.BIGINT)
            .update();
        log.debug("removeLinkInfoById(): {} rows were updated", updated);
        return oldLinkInfo;
    }

    @Override
    public Optional<GitHubLinkInfo> findLinkInfoByUrl(URI url) {
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
