package edu.java.client;

import edu.java.dto.GitHubRepoResponse;
import reactor.core.publisher.Mono;

public interface AsyncGitHubClient extends Client {

    Mono<GitHubRepoResponse> getRepositoryByOwnerNameAndRepoName(String ownerName, String repoName);

}
