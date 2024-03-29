package edu.java.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GitHubApiPaths {

    public static final String OWNER_NAME_PARAM = "ownerName";
    public static final String REPO_NAME_PARAM = "repoName";
    public static final String GET_REPOSITORY = String.format("/repos/{%s}/{%s}", OWNER_NAME_PARAM, REPO_NAME_PARAM);

    public static final String GET_REPOSITORY_EVENTS = GET_REPOSITORY + "/events";

    public static final String PER_PAGE_EVENTS_PARAMETER = "per_page";

}
