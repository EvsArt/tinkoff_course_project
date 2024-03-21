package edu.java.service;

import edu.java.api.dto.AddLinkRequest;
import edu.java.api.dto.LinkResponse;
import edu.java.api.dto.ListLinksResponse;
import edu.java.api.dto.RemoveLinkRequest;
import edu.java.model.Link;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinksTransformServiceImpl implements LinksTransformService {

    private final LinksParsingServiceImpl linksParsingService;

    @Autowired
    public LinksTransformServiceImpl(LinksParsingServiceImpl linksParsingService) {
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
