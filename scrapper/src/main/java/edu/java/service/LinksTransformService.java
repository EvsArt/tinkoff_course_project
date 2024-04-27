package edu.java.service;

import edu.java.dto.bot.AddLinkRequest;
import edu.java.dto.bot.LinkResponse;
import edu.java.dto.bot.ListLinksResponse;
import edu.java.dto.bot.RemoveLinkRequest;
import edu.java.model.entity.Link;
import java.util.List;

public interface LinksTransformService {
    ListLinksResponse toListLinksResponse(List<Link> links);

    Link toLink(AddLinkRequest addLinkRequest);

    Link toLink(RemoveLinkRequest removeLinkRequest);

    LinkResponse toLinkResponse(Link link);

}
