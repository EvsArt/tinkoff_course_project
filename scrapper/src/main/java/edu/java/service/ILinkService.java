package edu.java.service;

import edu.java.model.Link;
import edu.java.model.TgChat;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ILinkService implements LinkService {

    private final LinkRepository linkRepository;
    private final TgChatRepository chatRepository;

    public ILinkService(LinkRepository linkRepository, TgChatRepository chatRepository) {
        this.linkRepository = linkRepository;
        this.chatRepository = chatRepository;
    }

    @Override
    public Link addLink(long tgChatId, URI url, String name) {
        Link newLink = new Link(url, name, OffsetDateTime.now(), OffsetDateTime.now(), OffsetDateTime.now());
        TgChat chat = chatRepository.findTgChatByChatId(tgChatId)
            .orElseThrow(() -> new IllegalArgumentException("Chat not register!"));
        newLink.getTgChats().add(chat);

        return linkRepository.insertLink(newLink).get();
    }

    @Override
    public Link removeLink(long tgChatId, URI url) {
        return linkRepository.removeLinkByTgChatIdAndUri(tgChatId, url).orElseGet(Link::new);
    }

    @Override
    public List<Link> findAll(long tgChatId) {
        return linkRepository.findLinksByTgChatId(tgChatId);
    }

}
