package edu.java.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GitHubApiPaths {

    public static final String GET_REPOSITORY = "/repos/{ownerName}/{repoName}";

}
