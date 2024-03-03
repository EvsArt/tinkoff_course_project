package edu.java.botClient;

import edu.java.botClient.dto.LinkUpdateRequest;
import edu.java.configuration.ApiConfig;
import edu.java.constants.BotApiPaths;
import edu.java.exceptions.status.ForbiddenException;
import edu.java.exceptions.status.MovedPermanentlyException;
import edu.java.exceptions.status.ResourceNotFoundException;
import edu.java.exceptions.status.ServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
public class DefaultBotClient implements BotClient {

    private final WebClient webClient;
    private final ApiConfig.BotConfig config;

    private DefaultBotClient(WebClient webClient, ApiConfig.BotConfig config) {
        this.webClient = webClient;
        this.config = config;
        log.info("Created Bot Client");
    }

    public static DefaultBotClient create(ApiConfig.BotConfig config) {
        WebClient webClient = buildWebClient(config);
        return new DefaultBotClient(webClient, config);
    }

    private static WebClient buildWebClient(ApiConfig.BotConfig config) {

        HttpClient client = HttpClient
            .create()
            .responseTimeout(config.connectionTimeout());

        return WebClient.builder()
            .baseUrl(config.url().toString())
            .clientConnector(new ReactorClientHttpConnector(client))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultStatusHandler(
                status -> status.isSameCodeAs(HttpStatus.MOVED_PERMANENTLY),
                resp -> Mono.error(MovedPermanentlyException::new)
            )
            .defaultStatusHandler(
                status -> status.isSameCodeAs(HttpStatus.FORBIDDEN),
                resp -> Mono.error(ForbiddenException::new)
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
    public ResponseEntity<Void> postUpdates(LinkUpdateRequest updateRequest) {
        Mono<Void> res = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(BotApiPaths.POST_UPDATE)
                .build()
            )
            .retrieve()
            .bodyToMono(Void.class);
        return ResponseEntity.ok(res.block());

    }

}
