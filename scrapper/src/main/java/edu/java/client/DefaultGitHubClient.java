package edu.java.client;

import edu.java.configuration.ApiConfig;
import edu.java.constants.GitHubApiPaths;
import edu.java.dto.GitHubRepoResponse;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class DefaultGitHubClient implements GitHubClient {

    private final WebClient webClient;
    private final ApiConfig.GitHubConfig config;

    private DefaultGitHubClient(WebClient webClient, ApiConfig.GitHubConfig config) {
        this.webClient = webClient;
        this.config = config;
        log.info("Created GitHub Client");
    }

    public static GitHubClient create(ApiConfig.GitHubConfig config) {
        WebClient webClient = buildWebClient(config.url());
        return new DefaultGitHubClient(webClient, config);
    }

    @Override
    public Mono<GitHubRepoResponse> getRepositoryByOwnerNameAndRepoName(String ownerName, String repoName) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(GitHubApiPaths.GET_REPOSITORY)
                .queryParams(config.uriParameters())
                .build(ownerName, repoName)
            )
            .retrieve()
            .bodyToMono(GitHubRepoResponse.class)
            .onErrorResume(e -> Mono.empty());
    }

    private static WebClient buildWebClient(URL url) {
        return WebClient.builder()
            .baseUrl(url.toString())
            .build();
    }

}
