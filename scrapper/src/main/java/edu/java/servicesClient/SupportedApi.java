package edu.java.servicesClient;

import java.util.Arrays;
import java.util.List;
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
        List<SupportedApi> foundApis = Arrays.stream(values())
            .filter(value -> value.linkPattern.matcher(link).matches())
            .limit(1)
            .toList();
        if (foundApis.isEmpty()) {
            throw new IllegalArgumentException("Unknown link format: " + link);
        }
        return foundApis.get(0);
    }

    public Pattern getLinkPattern() {
        return linkPattern;
    }

}
