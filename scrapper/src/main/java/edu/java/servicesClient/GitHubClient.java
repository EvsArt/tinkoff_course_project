package edu.java.servicesClient;

import edu.java.dto.github.GitHubRepoEventResponse;
import edu.java.dto.github.GitHubRepoRequest;
import edu.java.dto.github.GitHubRepoResponse;
import reactor.core.publisher.Mono;

public interface GitHubClient extends Client {

    Mono<GitHubRepoResponse> getRepository(GitHubRepoRequest request);

    Mono<GitHubRepoEventResponse> getLastRepositoryEvent(GitHubRepoRequest request);

}
