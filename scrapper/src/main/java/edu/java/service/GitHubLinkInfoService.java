package edu.java.service;

import edu.java.model.entity.GitHubLinkInfo;
import edu.java.model.entity.Link;
import java.net.URI;

public interface GitHubLinkInfoService {

    GitHubLinkInfo findLinkInfoByLinkId(long linkId);

    GitHubLinkInfo findLinkInfoByLinkUrl(URI url);

    GitHubLinkInfo addLinkInfo(Link link);

    GitHubLinkInfo updateLinkInfo(long linkId, GitHubLinkInfo linkInfo);

    GitHubLinkInfo removeLinkInfoByLink(Link link);
}
