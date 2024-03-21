package edu.java.service;

import edu.java.dto.GitHubRepoRequest;
import edu.java.dto.StackOverflowQuestionRequest;

public interface LinksParsingService {

    GitHubRepoRequest getGitHubRepoRequestByLink(String link);

    StackOverflowQuestionRequest getStackOverFlowQuestionRequestByLink(String link);

    String getLinkName(String link);

}
