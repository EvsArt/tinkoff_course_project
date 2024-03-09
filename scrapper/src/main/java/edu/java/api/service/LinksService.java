package edu.java.api.service;

import edu.java.api.dto.AddLinkRequest;
import edu.java.api.dto.LinkResponse;
import edu.java.api.dto.ListLinksResponse;
import edu.java.api.dto.RemoveLinkRequest;
import edu.java.api.exceptions.ChatNotExistException;
import edu.java.api.exceptions.LinkNotExistsException;
import edu.java.api.repository.InMemoryChatRepository;
import edu.java.api.repository.InMemoryLinksRepository;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinksService {

    private final InMemoryLinksRepository linksRepository;
    private final InMemoryChatRepository chatRepository;

    @Autowired
    public LinksService(InMemoryLinksRepository linksRepository, InMemoryChatRepository chatRepository) {
        this.linksRepository = linksRepository;
        this.chatRepository = chatRepository;
    }

    public ListLinksResponse getListLinksResponseByTgChatId(Long tgChatId) {
        if (!chatRepository.isExists(tgChatId)) {
            throw new ChatNotExistException();
        }
        return new ListLinksResponse(
            linksRepository.getLinksByChatId(tgChatId).stream()
                .map(this::linkToLinkResponse)
                .toList()
        );
    }

    public LinkResponse saveLink(Long chatId, AddLinkRequest requestBody) {
        if (!chatRepository.isExists(chatId)) {
            throw new ChatNotExistException();
        }
        linksRepository.addLink(chatId, addLinkRequestToLink(requestBody));
        return new LinkResponse(chatId, requestBody.getLink());
    }

    public LinkResponse removeLink(Long tgChatId, RemoveLinkRequest requestBody) throws LinkNotExistsException {
        if (!chatRepository.isExists(tgChatId)) {
            throw new ChatNotExistException();
        }
        if (!linksRepository.removeLink(tgChatId, removeLinkRequestToLink(requestBody))) {
            throw new LinkNotExistsException();
        }
        return new LinkResponse(tgChatId, requestBody.getLink());
    }

    public LinkResponse linkToLinkResponse(InMemoryLinksRepository.Link link) {
        return new LinkResponse(link.id(), link.url());
    }

    // Will be edited with database adding
    public InMemoryLinksRepository.Link addLinkRequestToLink(AddLinkRequest request) {
        return new InMemoryLinksRepository.Link(new Random().nextLong(), request.getLink());
    }

    public InMemoryLinksRepository.Link removeLinkRequestToLink(RemoveLinkRequest request) {
        return new InMemoryLinksRepository.Link(new Random().nextLong(), request.getLink());
    }
}
