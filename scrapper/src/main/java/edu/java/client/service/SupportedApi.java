package edu.java.client.service;

import java.util.Arrays;
import java.util.regex.Pattern;

public enum SupportedApi {

    GITHUB_REPO(Pattern.compile("^(https?://)?github\\.com/(?<ownerName>\\w+)/(?<repoName>\\w+)$")),
    STACKOVERFLOW_QUESTION(Pattern.compile(
        "^(https?://)?stackoverflow\\.com/questions/(?<questionId>\\d+)/(?<questionName>[\\w-]+)$"));

    private final Pattern linkPattern;

    SupportedApi(Pattern linkPattern) {
        this.linkPattern = linkPattern;
    }

    public static SupportedApi getApiByLink(String link) {
        return Arrays.stream(values())
            .filter(value -> value.linkPattern.matcher(link).matches())
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown link format: " + link));
    }

    public Pattern getLinkPattern() {
        return linkPattern;
    }

}
