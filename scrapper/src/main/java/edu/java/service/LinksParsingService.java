package edu.java.service;

import edu.java.dto.github.GitHubRepoRequest;
import edu.java.dto.stackoverflow.StackOverflowQuestionRequest;

public interface LinksParsingService {

    GitHubRepoRequest getGitHubRepoRequestByLink(String link);

    StackOverflowQuestionRequest getStackOverFlowQuestionRequestByLink(String link);

    String getLinkName(String link);

}
