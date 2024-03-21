package edu.java.bot.scrapperClient.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.constants.Headers;
import edu.java.bot.constants.ScrapperApiPaths;
import edu.java.bot.scrapperClient.config.ScrapperConfig;
import edu.java.bot.scrapperClient.dto.AddLinkRequest;
import edu.java.bot.scrapperClient.dto.DeleteChatResponse;
import edu.java.bot.scrapperClient.dto.LinkResponse;
import edu.java.bot.scrapperClient.dto.ListLinksResponse;
import edu.java.bot.scrapperClient.dto.RegisterChatResponse;
import edu.java.bot.scrapperClient.dto.RemoveLinkRequest;
import edu.java.bot.scrapperClient.exceptions.status.BadRequestException;
import edu.java.bot.scrapperClient.exceptions.status.ResourceNotFoundException;
import edu.java.bot.scrapperClient.exceptions.status.ServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
public class DefaultScrapperClient implements ScrapperClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient;
    private final ScrapperConfig config;

    private DefaultScrapperClient(WebClient webClient, ScrapperConfig config) {
        this.webClient = webClient;
        this.config = config;
        log.info("Created Scrapper Client");
    }

    public static ScrapperClient create(ScrapperConfig config) {
        WebClient webClient = buildWebClient(config);
        return new DefaultScrapperClient(webClient, config);
    }

    private static WebClient buildWebClient(ScrapperConfig config) {

        HttpClient client = HttpClient
            .create()
            .responseTimeout(config.connectionTimeout());

        return WebClient.builder()
            .baseUrl(config.url().toString())
            .clientConnector(new ReactorClientHttpConnector(client))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultStatusHandler(
                status -> status.isSameCodeAs(HttpStatus.BAD_REQUEST),
                resp -> Mono.error(BadRequestException::new)
            )
            .defaultStatusHandler(
                status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                resp -> Mono.error(ResourceNotFoundException::new)
            )
            .defaultStatusHandler(
                HttpStatusCode::is5xxServerError,
                resp -> Mono.error(ServerErrorException::new)
            )
            .build();
    }

    @Override
    public Mono<RegisterChatResponse> registerChat(Long id) {
        return webClient.post()
            .uri(uriBuilder -> uriBuilder
                .path(ScrapperApiPaths.CHAT)
                .build(id)
            )
            .retrieve()
            .bodyToMono(RegisterChatResponse.class);
    }

    @Override
    public Mono<DeleteChatResponse> deleteChat(Long id) {
        return webClient.delete()
            .uri(uriBuilder -> uriBuilder
                .path(ScrapperApiPaths.CHAT)
                .build(id)
            )
            .retrieve()
            .bodyToMono(DeleteChatResponse.class);
    }

    @Override
    public Mono<ListLinksResponse> getLinks(Long tgChatId) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(ScrapperApiPaths.LINKS)
                .build()
            )
            .header(Headers.TG_CHAT_ID, String.valueOf(tgChatId))
            .retrieve()
            .bodyToMono(ListLinksResponse.class);
    }

    @Override
    public Mono<LinkResponse> addLink(Long tgChatId, AddLinkRequest addLinkRequest) {
        try {
            return webClient.post()
                .uri(uriBuilder -> uriBuilder
                    .path(ScrapperApiPaths.LINKS)
                    .build()
                )
                .header(Headers.TG_CHAT_ID, String.valueOf(tgChatId))
                .body(BodyInserters.fromValue(objectMapper.writer().writeValueAsString(addLinkRequest)))
                .retrieve()
                .bodyToMono(LinkResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<LinkResponse> removeLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        try {
            return webClient.method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                    .path(ScrapperApiPaths.LINKS)
                    .build()
                )
                .header(Headers.TG_CHAT_ID, String.valueOf(tgChatId))
                .body(BodyInserters.fromValue(objectMapper.writer().writeValueAsString(removeLinkRequest)))
                .retrieve()
                .bodyToMono(LinkResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
