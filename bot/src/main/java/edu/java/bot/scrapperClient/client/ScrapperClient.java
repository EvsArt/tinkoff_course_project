package edu.java.bot.scrapperClient.client;

import edu.java.bot.scrapperClient.dto.AddLinkRequest;
import edu.java.bot.scrapperClient.dto.DeleteChatResponse;
import edu.java.bot.scrapperClient.dto.LinkResponse;
import edu.java.bot.scrapperClient.dto.ListLinksResponse;
import edu.java.bot.scrapperClient.dto.RegisterChatResponse;
import edu.java.bot.scrapperClient.dto.RemoveLinkRequest;
import reactor.core.publisher.Mono;

public interface ScrapperClient {

    Mono<RegisterChatResponse> registerChat(Long id);
    Mono<DeleteChatResponse> deleteChat(Long id);

    Mono<ListLinksResponse> getLinks(Long tgChatId);
    Mono<LinkResponse> addLink(Long tgChatId, AddLinkRequest addLinkRequest);
    Mono<LinkResponse> removeLink(Long tgChatId, RemoveLinkRequest removeLinkRequest);

}
