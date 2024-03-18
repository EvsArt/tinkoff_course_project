package edu.java.service;

import edu.java.model.GitHubLinkInfo;
import edu.java.model.Link;
import java.net.URI;

public interface GitHubLinkInfoService {

    GitHubLinkInfo findLinkInfoByLinkId(long linkId);

    GitHubLinkInfo findLinkInfoByLinkUrl(URI url);

    GitHubLinkInfo addLinkInfo(Link link);

    GitHubLinkInfo updateLinkInfo(long linkId, GitHubLinkInfo linkInfo);

    GitHubLinkInfo removeLinkInfoByLink(Link link);
}
