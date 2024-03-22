package edu.java.domain.jooqRepository;

import edu.java.domain.LinkRepository;
import edu.java.model.entity.Link;
import edu.java.model.entity.TgChat;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.domain.jooq.Tables.LINK;
import static edu.java.domain.jooq.Tables.LINK_TG_CHAT;

@Slf4j
@Repository
public class JooqLinkRepository implements LinkRepository {

    private final DefaultDSLContext dsl;
    private final JooqAssociativeTableRepository associativeTableRepository;
    private final JooqTgChatRepository tgChatRepository;

    public JooqLinkRepository(
        DefaultDSLContext dsl,
        JooqAssociativeTableRepository associativeTableRepository,
        JooqTgChatRepository tgChatRepository
    ) {
        this.dsl = dsl;
        this.associativeTableRepository = associativeTableRepository;
        this.tgChatRepository = tgChatRepository;
    }

    @Override
    public Optional<Link> insertLink(Link link) {
        log.debug("insertLink() was called with link={}", link);
        dsl.insertInto(LINK,
                LINK.NAME, LINK.URL, LINK.CREATED_AT, LINK.LAST_CHECK_TIME, LINK.LAST_UPDATE_TIME
            )
            .values(
                link.getName(),
                link.getUrl().toString(),
                link.getCreatedAt(),
                link.getLastCheckTime(),
                link.getLastUpdateTime()
            )
            .execute();
        Optional<Link> savedLink = findLinkByURL(link.getUrl());
        // saving chats by link
        savedLink.ifPresent(value -> {
            log.info("insertLink(): link saved link={}", value);
            associativeTableRepository.saveLinkAndChatIds(value.getId(), link);
        });

        return findLinkByURL(link.getUrl());
    }

    @Override
    public Optional<Link> updateLink(Long id, Link newLink) {
        log.debug("updateLink() was called with id={}: link={}", id, newLink);
        Optional<Link> oldLink = findLinkById(id);
        if (oldLink.isEmpty()) {
            return oldLink;
        }
        int updated = dsl
            .update(LINK)
            .set(LINK.NAME, newLink.getName())
            .set(LINK.URL, newLink.getUrl().toString())
            .set(LINK.CREATED_AT, newLink.getCreatedAt())
            .set(LINK.LAST_UPDATE_TIME, newLink.getLastUpdateTime())
            .set(LINK.LAST_CHECK_TIME, newLink.getLastCheckTime())
            .where(LINK.ID.eq(id))
            .execute();

        // creating rows in associative table
        log.debug("newLink: {}", newLink);
        newLink.getTgChats().stream()
            .filter(chat -> !oldLink.get().getTgChats().contains(chat))
            .forEach(chat -> associativeTableRepository.saveLinkAndChatIds(id, chat.getId()));
        // remove unused chats from associative table
        log.debug("oldLink: {}", oldLink.get());
        oldLink.get().getTgChats().stream()
            .filter(chat -> !newLink.getTgChats().contains(chat))
            .forEach(chat -> associativeTableRepository.removeLinkAndChatIds(id, chat.getId()));

        log.info("updateLink(): {} rows in link table were updated", updated);
        return findLinkById(id);
    }

    @Override
    public Optional<Link> removeLinkById(Long id) {
        log.debug("removeLinkById() was called with id={}", id);
        Optional<Link> oldLink = findLinkById(id);
        int updated = dsl
            .deleteFrom(LINK)
            .where(LINK.ID.eq(id))
            .execute();
        oldLink.ifPresent(it -> it.getTgChats()
            .forEach(chat -> associativeTableRepository.removeLinkAndChatIds(id, chat.getId())));
        log.debug("removeLinkById(): {} rows were updated", updated);
        return oldLink;
    }

    @Override
    public Optional<Link> findLinkById(Long id) {
        log.debug("findLinkById() was called with id={}", id);
        Optional<Link> foundLink = dsl
            .select()
            .from(LINK)
            .where(LINK.ID.eq(id))
            .fetchOptionalInto(Link.class);
        if (foundLink.isEmpty()) {
            return foundLink;
        }
        foundLink.get().setTgChats(new HashSet<>(tgChatRepository.findTgChatsByLinkId(id)));
        return foundLink;
    }

    @Override
    public Optional<Link> findLinkByURL(URI url) {
        log.debug("findLinkByUrl() was called with url={}", url);
        Optional<Link> link = dsl
            .select()
            .from(LINK)
            .where(LINK.URL.eq(url.toString()))
            .fetchOptionalInto(Link.class);
        if (link.isEmpty()) {
            return link;
        }
        link.get().setTgChats(new HashSet<>(tgChatRepository.findTgChatsByLinkId(link.get().getId())));
        return link;
    }

    @Override
    public List<Link> findAllLinks() {
        log.debug("findAllLinks() was called");
        List<Link> links = dsl
            .select()
            .from(LINK)
            .fetchInto(Link.class);
        links.forEach(link -> link.setTgChats(new HashSet<>(tgChatRepository.findTgChatsByLinkId(link.getId()))));
        return links;
    }

    @Override
    public List<Link> findLinksByTgChatId(Long id) {
        log.debug("findLinksByTgChatId() was called with id={}", id);
        // getting links id from associative table
        List<Link> links = dsl
            .select(LINK)
            .from(LINK_TG_CHAT)
            .join(LINK).on(LINK.ID.eq(LINK_TG_CHAT.LINK_ID))
            .where(LINK_TG_CHAT.TG_CHAT_ID.eq(id))
            .fetchInto(Link.class);
        links.forEach(
            link -> link.setTgChats(new HashSet<>(tgChatRepository.findTgChatsByLinkId(link.getId())))
        );
        return links;
    }

    @Override
    public List<Link> removeLinksByTgChatId(Long id) {
        log.debug("removeLinksByTgChatId() was called with id={}", id);
        List<Link> deletedLinks = associativeTableRepository.removeLinkAndChatIdsByTgChatId(id).stream()
            .map(linkId -> findLinkById(linkId).get())
            .toList();
        removeLinksWithoutChat(deletedLinks);
        return deletedLinks;
    }

    @Override
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

    @Override
    public List<Link> findAllWhereLastCheckTimeBefore(OffsetDateTime dateTime) {
        log.debug("findAllWhereLastCheckTimeBefore() was called with dateTime={}", dateTime);
        List<Link> links = dsl.select()
            .from(LINK)
            .where(LINK.LAST_CHECK_TIME.lessThan(dateTime))
            .fetchInto(Link.class);
        links.forEach(link -> link.setTgChats(new HashSet<>(tgChatRepository.findTgChatsByLinkId(link.getId()))));
        return links;
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
