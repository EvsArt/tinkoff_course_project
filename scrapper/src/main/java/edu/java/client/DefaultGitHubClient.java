package edu.java.client;

import edu.java.configuration.ApiConfig;
import edu.java.constants.GitHubApiPaths;
import edu.java.dto.GitHubRepoResponse;
import edu.java.exceptions.status.ForbiddenException;
import edu.java.exceptions.status.MovedPermanentlyException;
import edu.java.exceptions.status.ResourceNotFoundException;
import edu.java.exceptions.status.ServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
public class DefaultGitHubClient implements AsyncGitHubClient {

    private final WebClient webClient;
    private final ApiConfig.GitHubConfig config;

    private DefaultGitHubClient(WebClient webClient, ApiConfig.GitHubConfig config) {
        this.webClient = webClient;
        this.config = config;
        log.info("Created GitHub Client");
    }

    public static AsyncGitHubClient create(ApiConfig.GitHubConfig config) {
        WebClient webClient = buildWebClient(config);
        return new DefaultGitHubClient(webClient, config);
    }

    /**
     * Returns repository info by its owner's name and repository name
     *
     * @param ownerName is repository owner's name
     * @param repoName  is repository name
     * @return repository info
     */
    @Override
    public Mono<GitHubRepoResponse> getRepositoryByOwnerNameAndRepoName(String ownerName, String repoName) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(GitHubApiPaths.GET_REPOSITORY)
                .queryParams(config.uriParameters())
                .build(ownerName, repoName)
            )
            .retrieve()
            .bodyToMono(GitHubRepoResponse.class);
    }

    private static WebClient buildWebClient(ApiConfig.GitHubConfig config) {

        HttpClient client = HttpClient
            .create()
            .responseTimeout(config.connectionTimeout());

        return WebClient.builder()
            .baseUrl(config.url().toString())
            .clientConnector(new ReactorClientHttpConnector(client))
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

}
