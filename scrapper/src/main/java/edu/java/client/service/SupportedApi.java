package edu.java.client.service;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public enum SupportedApi {

    GITHUB_REPO(Pattern.compile("^(https?://)?github\\.com/(?<ownerName>\\w+)/(?<repoName>\\w+)$")),
    STACKOVERFLOW_QUESTION(Pattern.compile("^(https?://)?stackoverflow\\.com/questions/(?<questionId>\\d+)/(\\w+)$"));

    private final Pattern linkPattern;

    SupportedApi(Pattern linkPattern) {
        this.linkPattern = linkPattern;
    }

    public static SupportedApi getApiByLink(URI link) {
        List<SupportedApi> foundApis = Arrays.stream(values())
            .filter(value -> value.linkPattern.matcher(link.toString()).matches())
            .limit(1)
            .toList();
        if (foundApis.isEmpty()) {
            throw new IllegalArgumentException("Unknown link format: " + link.toString());
        }
        return foundApis.get(0);
    }

    public Pattern getLinkPattern() {
        return linkPattern;
    }

}
