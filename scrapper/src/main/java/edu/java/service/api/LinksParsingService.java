package edu.java.service.api;

import edu.java.client.service.SupportedApi;
import edu.java.dto.GitHubRepoRequest;
import edu.java.dto.StackOverflowQuestionRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class LinksParsingService {

    public GitHubRepoRequest getGitHubRepoRequestByLink(String link) {
        Pattern linkPattern = SupportedApi.GITHUB_REPO.getLinkPattern();
        if (!linkPattern.matcher(link).matches()) {
            throw new IllegalArgumentException("Illegal GitHub repo link format!");
        }

        Matcher matcher = linkPattern.matcher(link);
        return new GitHubRepoRequest(
            matcher.group("ownerName"),
            matcher.group("repoName")
        );
    }

    public StackOverflowQuestionRequest getQuestionRequestByLink(String link) {
        Pattern linkPattern = SupportedApi.STACKOVERFLOW_QUESTION.getLinkPattern();
        if (!linkPattern.matcher(link).matches()) {
            throw new IllegalArgumentException("Illegal StackOverFlow question link format!");
        }

        Matcher matcher = linkPattern.matcher(link);
        return new StackOverflowQuestionRequest(
            Long.parseLong(matcher.group("questionId"))
        );
    }

}
