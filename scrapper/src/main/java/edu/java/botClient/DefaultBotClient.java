package edu.java.botClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.configuration.BotConfig;
import edu.java.constants.BotApiPaths;
import edu.java.dto.bot.LinkUpdateRequest;
import edu.java.dto.bot.PostUpdatesResponse;
import edu.java.exceptions.status.BadRequestException;
import edu.java.exceptions.status.ServerErrorException;
import edu.java.exceptions.status.TooManyRequestsException;
import edu.java.metrics.SentHttpUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
public class DefaultBotClient implements BotClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient;
    private final BotConfig config;

    protected DefaultBotClient(WebClient webClient, BotConfig config) {
        this.webClient = webClient;
        this.config = config;
        log.info("Created Bot Client");
    }

    public static DefaultBotClient create(BotConfig config) {
        WebClient webClient = buildWebClient(config);
        return new DefaultBotClient(webClient, config);
    }

    private static WebClient buildWebClient(BotConfig config) {

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
                status -> status.isSameCodeAs(HttpStatus.TOO_MANY_REQUESTS),
                resp -> Mono.error(TooManyRequestsException::new)
            )
            .defaultStatusHandler(
                HttpStatusCode::is5xxServerError,
                resp -> Mono.error(ServerErrorException::new)
            )
            .build();
    }

    @Override
    @SentHttpUpdate
    public Mono<PostUpdatesResponse> postUpdates(LinkUpdateRequest updateRequest) throws JsonProcessingException {
        return webClient.post()
            .uri(uriBuilder -> uriBuilder
                .path(BotApiPaths.UPDATES)
                .build()
            )
            .body(BodyInserters.fromValue(objectMapper.writer().writeValueAsString(updateRequest)))
            .retrieve()
            .bodyToMono(PostUpdatesResponse.class)
            .retryWhen(config.retry().toReactorRetry())
            .onErrorMap(it -> (Exceptions.isRetryExhausted(it)) ? it.getCause() : it);  // removing retryEx wrapper;
    }

}
