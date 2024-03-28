package edu.java.client;

import edu.java.configuration.ApiConfig;
import edu.java.constants.GitHubApiPaths;
import edu.java.dto.GitHubRepoEventResponse;
import edu.java.dto.GitHubRepoRequest;
import edu.java.dto.GitHubRepoResponse;
import edu.java.exceptions.status.ForbiddenException;
import edu.java.exceptions.status.MovedPermanentlyException;
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
public class DefaultGitHubClient implements GitHubClient {

    private final WebClient webClient;
    private final ApiConfig.GitHubConfig config;

    private DefaultGitHubClient(WebClient webClient, ApiConfig.GitHubConfig config) {
        this.webClient = webClient;
        this.config = config;
        log.info("Created GitHub Client");
    }

    public static DefaultGitHubClient create(ApiConfig.GitHubConfig config) {
        WebClient webClient = buildWebClient(config);
        return new DefaultGitHubClient(webClient, config);
    }

    private static WebClient buildWebClient(ApiConfig.GitHubConfig config) {

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

    /**
     * Returns repository info by its owner's name and repository name
     *
     * @param request is dto with repo owner and repo name
     * @return repository info
     */
    @Override
    public Mono<GitHubRepoResponse> getRepository(GitHubRepoRequest request) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(GitHubApiPaths.GET_REPOSITORY)
                .queryParams(config.uriParameters())
                .build(request.ownerName(), request.repositoryName())
            )
            .retrieve()
            .bodyToMono(GitHubRepoResponse.class)
            .retryWhen(config.retry().toReactorRetry());
    }

    @Override
    public Mono<GitHubRepoEventResponse> getLastRepositoryEvent(GitHubRepoRequest request) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(GitHubApiPaths.GET_REPOSITORY_EVENTS)
                .queryParams(config.uriParameters())
                .queryParam("per_page", 1)
                .build(request.ownerName(), request.repositoryName())
            )
            .retrieve()
            .bodyToMono(GitHubRepoEventResponse[].class)
            .map(arr -> arr[0]);    // its array with 1 element
    }

}
