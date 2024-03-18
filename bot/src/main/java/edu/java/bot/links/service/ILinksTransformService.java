package edu.java.bot.links.service;

import edu.java.bot.links.Link;
import edu.java.bot.scrapperClient.dto.LinkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ILinksTransformService implements LinksTransformService {

    private final ILinksParsingService linksParsingService;

    @Autowired
    public ILinksTransformService(ILinksParsingService linksParsingService) {
        this.linksParsingService = linksParsingService;
    }

    @Override
    public Link toLink(LinkResponse linkResponse) {
        return new Link(
            linkResponse.getUrl().toString(),
            linksParsingService.getLinkName(linkResponse.getUrl().toString())
        );
    }
}
