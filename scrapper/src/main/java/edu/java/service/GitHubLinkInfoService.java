package edu.java.service;

import edu.java.model.GitHubLinkInfo;
import java.net.URI;

public interface GitHubLinkInfoService {

    GitHubLinkInfo findLinkInfoByLinkId(long linkId);

    GitHubLinkInfo addLinkInfo(GitHubLinkInfo linkInfo);

    GitHubLinkInfo updateLinkInfo(long linkId, GitHubLinkInfo linkInfo);

    GitHubLinkInfo findLinkInfoByLinkUrl(URI url);
}
