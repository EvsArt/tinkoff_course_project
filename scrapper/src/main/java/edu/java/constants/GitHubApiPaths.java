package edu.java.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GitHubApiPaths {

    public static final String OWNER_NAME_PARAM = "ownerName";
    public static final String REPO_NAME_PARAM = "repoName";
    public static final String GET_REPOSITORY = String.format("/repos/{%s}/{%s}", OWNER_NAME_PARAM, REPO_NAME_PARAM);

}
