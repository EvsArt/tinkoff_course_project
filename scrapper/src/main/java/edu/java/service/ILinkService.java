package edu.java.service;

import edu.java.model.Link;
import edu.java.model.TgChat;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ILinkService implements LinkService {

    private final LinkRepository linkRepository;
    private final TgChatRepository chatRepository;

    public ILinkService(LinkRepository linkRepository, TgChatRepository chatRepository) {
        this.linkRepository = linkRepository;
        this.chatRepository = chatRepository;
    }

    @Override
    @Transactional
    public Link addLink(long tgChatId, URI url, String name) {
        Link newLink = linkRepository.findLinkByURL(url)
            .orElseGet(() -> new Link(url, name));
        TgChat chat = chatRepository.findTgChatByChatId(tgChatId)
            .orElseThrow(() -> new IllegalArgumentException("Chat not register!"));
        newLink.getTgChats().add(chat);
        if (newLink.getId() == null) {
            return linkRepository.insertLink(newLink).get();
        }
        return linkRepository.updateLink(newLink.getId(), newLink).get();
    }

    @Override
    @Transactional
    public Link removeLink(long tgChatId, URI url) {
        return linkRepository.removeLinkByTgChatIdAndUri(tgChatId, url).orElseGet(Link::new);
    }

    @Override
    @Transactional
    public List<Link> findAllByTgChatId(long tgChatId) {
        return linkRepository.findLinksByTgChatId(tgChatId);
    }

    @Override
    public List<Link> findAll() {
        return linkRepository.findAllLinks();
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
