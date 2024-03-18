package edu.java.client;

import edu.java.dto.GitHubRepoRequest;
import edu.java.dto.GitHubRepoResponse;
import reactor.core.publisher.Mono;

public interface GitHubClient extends Client {

    Mono<GitHubRepoResponse> getRepositoryByOwnerNameAndRepoName(GitHubRepoRequest request);

}