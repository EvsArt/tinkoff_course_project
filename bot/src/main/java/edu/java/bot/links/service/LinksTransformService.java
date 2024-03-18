package edu.java.bot.links.service;

import edu.java.bot.links.Link;
import edu.java.bot.scrapperClient.dto.LinkResponse;

public interface LinksTransformService {

    Link toLink(LinkResponse linkResponse);

}
