package edu.java.service.jpa;

import edu.java.domain.jpaRepository.JpaLinkRepository;
import edu.java.domain.jpaRepository.JpaTgChatRepository;
import edu.java.exceptions.ChatNotExistException;
import edu.java.exceptions.LinkNotExistsException;
import edu.java.model.entity.Link;
import edu.java.model.entity.TgChat;
import edu.java.service.LinkInfoService;
import edu.java.service.LinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
public class JpaLinkService implements LinkService {

    private final JpaLinkRepository linkRepository;
    private final JpaTgChatRepository chatRepository;
    private final LinkInfoService linkInfoService;

    public JpaLinkService(JpaLinkRepository linkRepository, JpaTgChatRepository chatRepository,
        LinkInfoService linkInfoService
    ) {
        this.linkRepository = linkRepository;
        this.chatRepository = chatRepository;
        this.linkInfoService = linkInfoService;
    }

    @Override
    public Link addLink(long tgChatId, URI url, String name) {
        log.debug("addLink() was called with tgChatId={}, url={}, name={}", tgChatId, url, name);
        Link newLink = linkRepository.findByUrl(url).orElse(new Link(url, name));
        TgChat chat = chatRepository.findTgChatByChatId(tgChatId).orElseThrow(IllegalArgumentException::new);
        newLink.add(chat);
        return linkRepository.save(newLink);
    }

    @Override
    @Transactional
    public Link removeLink(long tgChatId, URI url) {
        log.debug("removeLink() was called with tgChatId={}, url={}", tgChatId, url);
        TgChat chat = chatRepository.findTgChatByChatId(tgChatId).orElseThrow(ChatNotExistException::new);
        Link oldLink = linkRepository.findByUrl(url).orElseThrow(LinkNotExistsException::new);
        oldLink.remove(chat);
        linkRepository.save(oldLink);
        if(oldLink.getTgChats().isEmpty()) {
            linkRepository.delete(oldLink);
        }
        return oldLink;
    }

    @Override
    public List<Link> findAllByTgChatId(long tgChatId) {
        log.debug("findAllByTgChatId() was called with tgChatId={}", tgChatId);
        TgChat chat = chatRepository.findTgChatByChatId(tgChatId).orElseThrow(ChatNotExistException::new);
        return linkRepository.findByTgChatsContains(chat);
    }

    @Override
    public List<Link> findAll() {
        log.debug("findAll() was called");
        return linkRepository.findAll();
    }

    @Override
    public Link findByUrl(URI url) {
        log.debug("findByUrl() was called with url={}", url);
        return linkRepository.findByUrl(url).orElseThrow(LinkNotExistsException::new);
    }

    @Override
    public Link findById(Long id) {
        log.debug("findById() was called with id={}", id);
        return linkRepository.findById(id).orElseThrow(LinkNotExistsException::new);
    }

    @Override
    public List<Link> findAllWhereLastCheckTimeBefore(OffsetDateTime dateTime) {
        log.debug("findAllWhereLastCheckTimeBefore() was called with dateTime={}", dateTime);
        return linkRepository.findByLastCheckTimeIsBefore(dateTime);
    }

    @Override
    public Link setLastCheckTime(Long linkId, OffsetDateTime dateTime) {
        log.debug("setLastCheckTime() was called with linkId={}, dateTime={}", linkId, dateTime);
        Link link = linkRepository.findById(linkId).orElseThrow(LinkNotExistsException::new);
        link.setLastCheckTime(dateTime);
        return linkRepository.save(link);
    }

    @Override
    public Link setLastUpdateTime(Long linkId, OffsetDateTime dateTime) {
        log.debug("setLastUpdateTime() was called with linkId={}, dateTime={}", linkId, dateTime);
        Link link = linkRepository.findById(linkId).orElseThrow(LinkNotExistsException::new);
        link.setLastUpdateTime(dateTime);
        return linkRepository.save(link);
    }
}
