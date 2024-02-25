package edu.java.client;

import edu.java.configuration.ApiConfig;
import edu.java.constants.StackOverflowApiPaths;
import edu.java.dto.StackOverflowQuestionListResponse;
import edu.java.dto.StackOverflowQuestionResponse;
import edu.java.exceptions.status.BadRequestException;
import edu.java.exceptions.status.ForbiddenException;
import edu.java.exceptions.status.ResourceNotFoundException;
import edu.java.exceptions.status.ServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
public class DefaultStackOverflowClient implements StackOverflowClient {

    private final WebClient webClient;
    private final ApiConfig.StackOverflowConfig config;

    private DefaultStackOverflowClient(WebClient webClient, ApiConfig.StackOverflowConfig config) {
        this.webClient = webClient;
        this.config = config;
        log.info("Created StackOverFlow Client");
    }

    public static DefaultStackOverflowClient create(ApiConfig.StackOverflowConfig config) {
        WebClient webClient = buildWebClient(config);
        return new DefaultStackOverflowClient(webClient, config);
    }

    /**
     * Returns question info by its id
     *
     * @param id is question id
     * @return question info
     */
    @Override
    public Mono<StackOverflowQuestionResponse> getQuestionById(Long id) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(StackOverflowApiPaths.GET_QUESTION)
                .queryParams(config.uriParameters())
                .build(id)
            )
            .retrieve()
            .onStatus(
                status -> status.isSameCodeAs(HttpStatus.BAD_REQUEST),
                resp -> Mono.error(BadRequestException::new)
            )
            .onStatus(
                status -> status.isSameCodeAs(HttpStatus.FORBIDDEN),
                resp -> Mono.error(ForbiddenException::new)
            )
            .onStatus(
                status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                resp -> Mono.error(ResourceNotFoundException::new)
            )
            .onStatus(HttpStatusCode::is5xxServerError, resp -> Mono.error(ServerErrorException::new))
            .bodyToMono(StackOverflowQuestionListResponse.class)
            .map(it -> it.items().getFirst());
    }

    private static WebClient buildWebClient(ApiConfig.StackOverflowConfig config) {
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

}
