package edu.java.service;

import edu.java.dto.bot.AddLinkRequest;
import edu.java.dto.bot.LinkResponse;
import edu.java.dto.bot.ListLinksResponse;
import edu.java.dto.bot.RemoveLinkRequest;
import edu.java.model.entity.Link;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinksTransformServiceImpl implements LinksTransformService {

    private final LinksParsingService linksParsingService;

    @Autowired
    public LinksTransformServiceImpl(LinksParsingService linksParsingService) {
        this.linksParsingService = linksParsingService;
    }

    @Override
    public ListLinksResponse toListLinksResponse(List<Link> links) {
        return new ListLinksResponse(
            links.stream()
                .map(link -> new LinkResponse(link.getUrl().toString()))
                .toList()
        );
    }

    @Override
    public Link toLink(AddLinkRequest addLinkRequest) {
        String url = addLinkRequest.getLink();
        return new Link(URI.create(url), linksParsingService.getLinkName(url));
    }

    @Override
    public Link toLink(RemoveLinkRequest removeLinkRequest) {
        String url = removeLinkRequest.getLink();
        return new Link(URI.create(url), linksParsingService.getLinkName(url));
    }

    @Override
    public LinkResponse toLinkResponse(Link link) {
        return new LinkResponse(link.getUrl().toString());
    }
}
