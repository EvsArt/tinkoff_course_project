package edu.java.service.jooq;

import edu.java.domain.jooqRepository.JooqLinkRepository;
import edu.java.domain.jooqRepository.JooqTgChatRepository;
import edu.java.exceptions.ChatNotExistException;
import edu.java.exceptions.LinkNotExistsException;
import edu.java.model.entity.Link;
import edu.java.model.entity.TgChat;
import edu.java.service.LinkInfoService;
import edu.java.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class JooqLinkService implements LinkService {

    private final JooqLinkRepository linkRepository;
    private final JooqTgChatRepository chatRepository;
    private final LinkInfoService linkInfoService;

    public JooqLinkService(
        JooqLinkRepository linkRepository,
        JooqTgChatRepository chatRepository,
        LinkInfoService linkInfoService
    ) {
        this.linkRepository = linkRepository;
        this.chatRepository = chatRepository;
        this.linkInfoService = linkInfoService;
    }

    @Override
    @Transactional
    public Link addLink(long tgChatId, URI url, String name) {
        log.debug("addLink() called with chatId={} and url={}", tgChatId, url);
        Link newLink = linkRepository.findLinkByURL(url)
            .orElseGet(() -> new Link(url, name));
        TgChat chat = chatRepository.findTgChatByChatId(tgChatId)
            .orElseThrow(() -> new IllegalArgumentException("Chat not register!"));
        newLink.getTgChats().add(chat);
        if (newLink.getId() != null) {
            return linkRepository.updateLink(newLink.getId(), newLink).get();
        }

        return linkRepository.insertLink(newLink).get();
    }

    @Override
    @Transactional
    public Link removeLink(long tgChatId, URI url) {
        boolean chatExists = chatRepository.findTgChatByChatId(tgChatId).isPresent();
        if (!chatExists) {
            throw new ChatNotExistException();
        }
        Link link = linkRepository.removeLinkByTgChatIdAndUri(tgChatId, url).orElseThrow(LinkNotExistsException::new);
        return link;
    }

    @Override
    @Transactional
    public List<Link> findAllByTgChatId(long tgChatId) {
        Optional<TgChat> existedChat = chatRepository.findTgChatByChatId(tgChatId);
        existedChat.orElseThrow(ChatNotExistException::new);

        return linkRepository.findLinksByTgChatId(existedChat.get().getId());
    }

    @Override
    public List<Link> findAll() {
        return linkRepository.findAllLinks();
    }

    @Override
    public Link findByUrl(URI url) {
        return linkRepository.findLinkByURL(url).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public Link findById(Long id) {
        return linkRepository.findLinkById(id).orElseThrow(LinkNotExistsException::new);
    }

    @Override
    public List<Link> findAllWhereLastCheckTimeBefore(OffsetDateTime dateTime) {
        return linkRepository.findAllWhereLastCheckTimeBefore(dateTime);
    }

    @Override
    public Link setLastCheckTime(Long linkId, OffsetDateTime dateTime) {
        Link newLink = linkRepository.findLinkById(linkId).orElseGet(Link::new);
        newLink.setLastCheckTime(dateTime);
        return linkRepository.updateLink(linkId, newLink).get();
    }

    @Override
    public Link setLastUpdateTime(Long linkId, OffsetDateTime dateTime) {
        Link newLink = linkRepository.findLinkById(linkId).orElseGet(Link::new);
        newLink.setLastUpdateTime(dateTime);
        return linkRepository.updateLink(linkId, newLink).get();
    }

}
