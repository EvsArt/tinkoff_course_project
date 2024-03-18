package edu.java.service.api;

import edu.java.client.service.SupportedApi;
import edu.java.dto.GitHubRepoRequest;
import edu.java.dto.StackOverflowQuestionRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class ILinksParsingService implements LinksParsingService {

    String repoNameRegExpGroup = "repoName";
    String ownerNameRegExpGroup = "ownerName";
    String questionIdRegExpGroup = "questionId";

    public GitHubRepoRequest getGitHubRepoRequestByLink(String link) {
        Pattern linkPattern = SupportedApi.GITHUB_REPO.getLinkPattern();
        if (!linkPattern.matcher(link).matches()) {
            throw new IllegalArgumentException("Wrong GitHub repo link format!");
        }

        Matcher matcher = linkPattern.matcher(link);
        matcher.matches();
        return new GitHubRepoRequest(
            matcher.group(ownerNameRegExpGroup),
            matcher.group(repoNameRegExpGroup)
        );
    }

    public StackOverflowQuestionRequest getQuestionRequestByLink(String link) {
        Pattern linkPattern = SupportedApi.STACKOVERFLOW_QUESTION.getLinkPattern();
        if (!linkPattern.matcher(link).matches()) {
            throw new IllegalArgumentException("Wrong StackOverFlow question link format!");
        }

        Matcher matcher = linkPattern.matcher(link);
        matcher.matches();
        return new StackOverflowQuestionRequest(
            Long.parseLong(matcher.group(questionIdRegExpGroup))
        );
    }

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
