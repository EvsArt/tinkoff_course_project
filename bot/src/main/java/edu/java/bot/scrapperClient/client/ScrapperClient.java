package edu.java.bot.scrapperClient.client;

import edu.java.bot.dto.scrapperClient.AddLinkRequest;
import edu.java.bot.dto.scrapperClient.DeleteChatResponse;
import edu.java.bot.dto.scrapperClient.LinkResponse;
import edu.java.bot.dto.scrapperClient.ListLinksResponse;
import edu.java.bot.dto.scrapperClient.RegisterChatResponse;
import edu.java.bot.dto.scrapperClient.RemoveLinkRequest;
import reactor.core.publisher.Mono;

public interface ScrapperClient {

    Mono<RegisterChatResponse> registerChat(Long id);

    Mono<DeleteChatResponse> deleteChat(Long id);

    Mono<ListLinksResponse> getLinks(Long tgChatId);

    Mono<LinkResponse> addLink(Long tgChatId, AddLinkRequest addLinkRequest);

    Mono<LinkResponse> removeLink(Long tgChatId, RemoveLinkRequest removeLinkRequest);

}
