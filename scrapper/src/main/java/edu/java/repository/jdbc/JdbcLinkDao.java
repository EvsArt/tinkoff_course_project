package edu.java.repository.jdbc;

import edu.java.model.Link;
import edu.java.model.TgChat;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import edu.java.service.SqlQueries;
import java.net.URI;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
public class JdbcLinkDao implements LinkRepository {

    private final JdbcClient jdbcClient;
    private final TgChatRepository tgChatRepository;
    private final JdbcAssociativeTableRepository associativeTableRepository;

    private final String linkTableName = SqlQueries.LINK_TABLE_NAME;
    private final List<String> linkFieldsNamesWithoutId = SqlQueries.LINK_FIELDS_NAMES_WITHOUT_ID;

    @Autowired
    public JdbcLinkDao(
        JdbcClient jdbcClient,
        TgChatRepository tgChatRepository,
        JdbcAssociativeTableRepository associativeTableRepository
    ) {
        this.jdbcClient = jdbcClient;
        this.tgChatRepository = tgChatRepository;
        this.associativeTableRepository = associativeTableRepository;
    }

    @Override
    @Transactional
    public Optional<Link> insertLink(Link link) {
        log.debug("insertLink() was called with link={}", link);
        String sql = SqlQueries.insertQuery(linkTableName, linkFieldsNamesWithoutId);
        KeyHolder idHolder = new GeneratedKeyHolder();
        int updated = jdbcClient.sql(sql)
            .param(SqlQueries.LINK_FIELD_NAME_NAME, link.getName(), Types.VARCHAR)
            .param(SqlQueries.LINK_FIELD_URL_NAME, link.getUrl(), Types.VARCHAR)
            .param(SqlQueries.LINK_FIELD_CREATED_AT_NAME, link.getCreatedAt(), Types.TIMESTAMP_WITH_TIMEZONE)
            .param(SqlQueries.LINK_FIELD_LAST_UPDATE_TIME_NAME, link.getLastUpdateTime(), Types.TIMESTAMP_WITH_TIMEZONE)
            .param(SqlQueries.LINK_FIELD_LAST_CHECK_TIME_NAME, link.getLastCheckTime(), Types.TIMESTAMP_WITH_TIMEZONE)
            .update(idHolder, SqlQueries.LINK_FIELD_ID_NAME);
        if (updated == 0) {
            return Optional.empty();
        }
        long id = idHolder.getKey().longValue();
        log.info("insertLink(): link saved with id={}", id);
        // saving chats by link
        associativeTableRepository.saveLinkAndChatIds(id, link);
        return findLinkById(id);
    }

    @Override
    @Transactional
    public Optional<Link> updateLink(Long id, Link newLink) {
        log.debug("updateLink() was called with id={}: link={}", id, newLink);
        Optional<Link> oldLink = findLinkById(id);
        if (oldLink.isEmpty()) {
            return oldLink;
        }
        String sql = SqlQueries.updateQuery(linkTableName, linkFieldsNamesWithoutId, SqlQueries.LINK_FIELD_ID_NAME);
        int updated = jdbcClient.sql(sql)
            .param(SqlQueries.LINK_FIELD_NAME_NAME, newLink.getName(), Types.VARCHAR)
            .param(SqlQueries.LINK_FIELD_URL_NAME, newLink.getUrl(), Types.VARCHAR)
            .param(SqlQueries.LINK_FIELD_CREATED_AT_NAME, newLink.getCreatedAt(), Types.TIMESTAMP_WITH_TIMEZONE)
            .param(
                SqlQueries.LINK_FIELD_LAST_UPDATE_TIME_NAME,
                newLink.getLastUpdateTime(),
                Types.TIMESTAMP_WITH_TIMEZONE
            )
            .param(
                SqlQueries.LINK_FIELD_LAST_CHECK_TIME_NAME,
                newLink.getLastCheckTime(),
                Types.TIMESTAMP_WITH_TIMEZONE
            )
            .param(SqlQueries.LINK_FIELD_ID_NAME, id, Types.BIGINT)  // id in searching condition
            .update();

        // creating rows in associative table
        log.debug("newLink: {}", newLink.getTgChats());
        newLink.getTgChats().stream()
            .filter(chat -> !oldLink.get().getTgChats().contains(chat))
            .forEach(chat -> associativeTableRepository.saveLinkAndChatIds(id, chat.getId()));
        // remove unused chats from associative table
        log.debug("oldLink: {}", oldLink.get().getTgChats());
        oldLink.get().getTgChats().stream()
            .filter(chat -> !newLink.getTgChats().contains(chat))
            .forEach(chat -> associativeTableRepository.removeLinkAndChatIds(id, chat.getId()));

        log.info("updateLink(): {} rows in link table were updated", updated);
        return findLinkById(id);
    }

    @Override
    @Transactional
    public Optional<Link> removeLinkById(Long id) {
        log.debug("removeLinkById() was called with id={}", id);
        String sql = SqlQueries.deleteQuery(linkTableName, SqlQueries.LINK_FIELD_ID_NAME);
        Optional<Link> oldLink = findLinkById(id);
        int updated = jdbcClient.sql(sql)
            .param(SqlQueries.LINK_FIELD_ID_NAME, id, Types.BIGINT)
            .update();
        oldLink.ifPresent(it -> it.getTgChats()
            .forEach(chat -> associativeTableRepository.removeLinkAndChatIds(id, chat.getId())));
        log.debug("removeLinkById(): {} rows were updated", updated);
        return oldLink;
    }

    @Override
    @Transactional
    public Optional<Link> findLinkById(Long id) {
        log.debug("findLinkById() was called with id={}", id);
        String sql = SqlQueries.findWhereQuery(linkTableName, SqlQueries.LINK_FIELD_ID_NAME);
        Optional<Link> link = jdbcClient.sql(sql)
            .param(SqlQueries.LINK_FIELD_ID_NAME, id, Types.BIGINT)
            .query(Link.class)
            .optional();
        if (link.isEmpty()) {
            return link;
        }
        link.get().setTgChats(tgChatRepository.findTgChatsByLinkId(id));
        return link;
    }

    @Override
    @Transactional
    public Optional<Link> findLinkByURL(URI url) {
        log.debug("findLinkByUrl() was called with url={}", url);
        String sql = SqlQueries.findWhereQuery(linkTableName, SqlQueries.LINK_FIELD_URL_NAME);
        Optional<Link> link = jdbcClient.sql(sql)
            .param(SqlQueries.LINK_FIELD_URL_NAME, url, Types.VARCHAR)
            .query(Link.class)
            .optional();
        if (link.isEmpty()) {
            return link;
        }
        link.get().setTgChats(tgChatRepository.findTgChatsByLinkId(link.get().getId()));
        return link;
    }

    @Override
    @Transactional
    public List<Link> findAllLinks() {
        log.debug("findAllLinks() was called");
        String sql = SqlQueries.findAllQuery(linkTableName);
        List<Link> links = jdbcClient.sql(sql)
            .query(Link.class).list();
        links.forEach(link -> link.setTgChats(tgChatRepository.findTgChatsByLinkId(link.getId())));
        return links;
    }

    @Override
    @Transactional
    public List<Link> findAllWhereLastCheckTimeBefore(OffsetDateTime dateTime) {
        log.debug("findAllWhereLastCheckTimeBefore() was called with dateTime={}", dateTime);
        String sql = SqlQueries.findWhereBeforeQuery(linkTableName, SqlQueries.LINK_FIELD_LAST_CHECK_TIME_NAME);
        List<Link> links = jdbcClient.sql(sql)
            .param(SqlQueries.LINK_FIELD_LAST_CHECK_TIME_NAME, dateTime, Types.TIMESTAMP_WITH_TIMEZONE)
            .query(Link.class).list();
        links.forEach(link -> link.setTgChats(tgChatRepository.findTgChatsByLinkId(link.getId())));
        return links;
    }

    @Override
    @Transactional
    public List<Link> findLinksByTgChatId(Long id) {
        log.debug("findLinksByTgChatId() was called with id={}", id);
        String sql =
            SqlQueries.findFieldWhereQuery(
                SqlQueries.LINK_TG_CHAT_TABLE_NAME,
                SqlQueries.LINK_TG_CHAT_FIELD_LINK_NAME,
                SqlQueries.LINK_TG_CHAT_FIELD_TG_CHAT_NAME
            );
        // getting links id from associative table
        List<Long> linksId = jdbcClient.sql(sql)
            .param(SqlQueries.LINK_TG_CHAT_FIELD_TG_CHAT_NAME, id, Types.BIGINT)
            .query(Long.class).list();

        return linksId.stream()
            .map(linkId -> findLinkById(linkId).orElse(new Link()))
            .filter(link -> link.getId() != null)   // skip not exist links
            .toList();
    }

    /**
     * Remove links from associative table and delete them if there are not any
     * chats that tracks them
     *
     * @param id id of tgChat
     * @return list of deleted links
     */
    @Override
    @Transactional
    public List<Link> removeLinksByTgChatId(Long id) {
        log.debug("removeLinksByTgChatId() was called with id={}", id);
        List<Link> deletedLinks = associativeTableRepository.removeLinkAndChatIdsByTgChatId(id).stream()
            .map(linkId -> findLinkById(linkId).get())
            .toList();
        removeLinksWithoutChat(deletedLinks);
        return deletedLinks;
    }

    @Override
    @Transactional
    public Optional<Link> removeLinkByTgChatIdAndUri(Long chatId, URI uri) {
        log.debug("removeLinkByTgChatIdAndUri() was called with chatId={} and uri={}", chatId, uri);
        Optional<Link> link = findLinkByURL(uri);
        Optional<TgChat> chat = tgChatRepository.findTgChatByChatId(chatId);
        if (link.isEmpty() || chat.isEmpty()) {
            return link;
        }
        associativeTableRepository.removeLinkAndChatIds(link.get().getId(), chat.get().getId());
        link = findLinkByURL(uri);  // update tgChat list in link var
        removeLinksWithoutChat(List.of(link.get())).size();
        return link;
    }

    /**
     * Remove links from links table if there are not any chats with them
     *
     * @param links list of links that should to be checked
     * @return list of deleted links
     */
    @Transactional
    public List<Link> removeLinksWithoutChat(List<Link> links) {
        log.debug("removeLinksWithoutChat() was called");

        return links.stream()
            .filter(link -> link.getTgChats().isEmpty())   // links that hasn't chats
            .map(link -> removeLinkById(link.getId()).get())
            .toList();
    }

}
