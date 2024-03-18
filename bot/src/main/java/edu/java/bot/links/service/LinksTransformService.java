package edu.java.bot.links.service;

import edu.java.bot.links.Link;
import edu.java.bot.scrapperClient.dto.AddLinkRequest;
import edu.java.bot.scrapperClient.dto.LinkResponse;
import edu.java.bot.scrapperClient.dto.RemoveLinkRequest;

public interface LinksTransformService {

    Link toLink(LinkResponse linkResponse);

    AddLinkRequest toAddLinkRequest(Link newLink);

    RemoveLinkRequest toRemoveLinkRequest(Link newLink);
}
