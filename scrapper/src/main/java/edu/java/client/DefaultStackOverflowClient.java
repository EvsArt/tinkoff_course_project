package edu.java.client;

import edu.java.configuration.ApiConfig;
import edu.java.constants.StackOverflowApiPaths;
import edu.java.dto.StackOverflowQuestionListResponse;
import edu.java.dto.StackOverflowQuestionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.net.URL;

@Slf4j
public class DefaultStackOverflowClient implements StackOverflowClient {

    private final WebClient webClient;
    private final ApiConfig.StackOverflowConfig config;

    private DefaultStackOverflowClient(WebClient webClient, ApiConfig.StackOverflowConfig config) {
        this.webClient = webClient;
        this.config = config;
        log.info("Created StackOverFlow Client");
    }

    public static StackOverflowClient create(ApiConfig.StackOverflowConfig config) {
        WebClient webClient = buildWebClient(config.url());
        return new DefaultStackOverflowClient(webClient, config);
    }

    @Override
    public Mono<StackOverflowQuestionResponse> getQuestionById(Long id) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(StackOverflowApiPaths.GET_QUESTION)
                .queryParams(config.uriParameters())
                .build(id)
            )
            .retrieve()
            .bodyToMono(StackOverflowQuestionListResponse.class)
            .onErrorResume(e -> Mono.empty())
            .mapNotNull(it -> it.items().getFirst());
    }

    private static WebClient buildWebClient(URL url) {
        return WebClient.builder()
            .baseUrl(url.toString())
            .build();
    }

}
