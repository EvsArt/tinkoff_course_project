package edu.java.bot.links.service;

import edu.java.bot.dto.scrapperClient.AddLinkRequest;
import edu.java.bot.dto.scrapperClient.LinkResponse;
import edu.java.bot.dto.scrapperClient.RemoveLinkRequest;
import edu.java.bot.links.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinksTransformService {

    private final LinksParsingService linksParsingService;

    @Autowired
    public LinksTransformService(LinksParsingService linksParsingService) {
        this.linksParsingService = linksParsingService;
    }

    public Link toLink(LinkResponse linkResponse) {
        return new Link(
            linkResponse.getUrl().toString(),
            linksParsingService.getLinkName(linkResponse.getUrl().toString())
        );
    }

    public AddLinkRequest toAddLinkRequest(Link newLink) {
        return new AddLinkRequest(newLink.url());
    }

    public RemoveLinkRequest toRemoveLinkRequest(Link newLink) {
        return new RemoveLinkRequest(newLink.url());
    }

}
