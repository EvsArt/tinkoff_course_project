package edu.java.bot.links.service;

import edu.java.bot.links.SupportedApi;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class LinksParsingService {

    String repoNameRegExpGroup = "repoName";
    String questionIdRegExpGroup = "questionId";

    public String getLinkName(String link) {
        SupportedApi api = SupportedApi.getApiByLink(link);
        return switch (api) {
            case GITHUB_REPO -> getGitHubRepoName(link);
            case STACKOVERFLOW_QUESTION -> getStackOverFlowQuestionName(link);
        };
    }

    public String getGitHubRepoName(String link) {
        Pattern linkPattern = SupportedApi.GITHUB_REPO.getLinkPattern();
        if (!linkPattern.matcher(link).matches()) {
            throw new IllegalArgumentException("Illegal GitHub repo link format!");
        }

        Matcher matcher = linkPattern.matcher(link);
        matcher.matches();
        return matcher.group(repoNameRegExpGroup);
    }

    public String getStackOverFlowQuestionName(String link) {
        Pattern linkPattern = SupportedApi.STACKOVERFLOW_QUESTION.getLinkPattern();
        if (!linkPattern.matcher(link).matches()) {
            throw new IllegalArgumentException("Illegal StackOverFlow question link format!");
        }

        Matcher matcher = linkPattern.matcher(link);
        matcher.matches();
        return matcher.group(questionIdRegExpGroup);
    }

}
