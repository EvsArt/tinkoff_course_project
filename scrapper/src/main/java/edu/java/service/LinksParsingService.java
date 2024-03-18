package edu.java.service;

import edu.java.dto.GitHubRepoRequest;
import edu.java.dto.StackOverflowQuestionRequest;

public interface LinksParsingService {

    GitHubRepoRequest getGitHubRepoRequestByLink(String link);

    StackOverflowQuestionRequest getQuestionRequestByLink(String link);

    String getLinkName(String link);

}
