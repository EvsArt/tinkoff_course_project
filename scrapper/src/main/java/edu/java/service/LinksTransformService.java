package edu.java.service;

import edu.java.api.dto.AddLinkRequest;
import edu.java.api.dto.LinkResponse;
import edu.java.api.dto.ListLinksResponse;
import edu.java.api.dto.RemoveLinkRequest;
import edu.java.model.entity.Link;
import java.util.List;

public interface LinksTransformService {
    ListLinksResponse toListLinksResponse(List<Link> links);

    Link toLink(AddLinkRequest addLinkRequest);

    Link toLink(RemoveLinkRequest removeLinkRequest);

    LinkResponse toLinkResponse(Link link);

}
