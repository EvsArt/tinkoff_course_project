package edu.java.api.controller;

import edu.java.api.constants.Headers;
import edu.java.api.dto.AddLinkRequest;
import edu.java.api.dto.LinkResponse;
import edu.java.api.dto.ListLinksResponse;
import edu.java.api.dto.RemoveLinkRequest;
import edu.java.exceptions.status.TooManyRequestsException;
import edu.java.model.entity.Link;
import edu.java.service.LinkInfoService;
import edu.java.service.LinkService;
import edu.java.service.LinksTransformService;
import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/links")
public class LinksController implements ILinksController {

    private final LinkService linkService;
    private final LinksTransformService linksTransformService;
    private final LinkInfoService linkInfoService;
    private final Bucket bucket;

    @Autowired
    public LinksController(
        LinkService linkService, LinksTransformService linksTransformService,
        LinkInfoService linkInfoService,
        @Qualifier("LinksRateLimitBucket") Bucket bucket
    ) {
        this.linkService = linkService;
        this.linksTransformService = linksTransformService;
        this.linkInfoService = linkInfoService;
        this.bucket = bucket;
    }

    @GetMapping
    public ListLinksResponse getLinks(@RequestHeader(Headers.TG_CHAT_ID) Long tgChatId) {
        if (!bucket.tryConsume(1)) {
            throw new TooManyRequestsException();
        }
        log.debug("Getting links by id {}", tgChatId);
        List<Link> links = linkService.findAllByTgChatId(tgChatId);
        return linksTransformService.toListLinksResponse(links);
    }

    @PostMapping
    public LinkResponse addLink(
        @RequestHeader(Headers.TG_CHAT_ID) Long tgChatId,
        @RequestBody @Valid AddLinkRequest requestBody
    ) {
        if (!bucket.tryConsume(1)) {
            throw new TooManyRequestsException();
        }
        log.debug("Adding link {} to id {}", requestBody, tgChatId);
        Link link = linksTransformService.toLink(requestBody);
        link = linkService.addLink(tgChatId, link.getUrl(), link.getName());
        linkInfoService.addLinkInfoByLink(link);
        return linksTransformService.toLinkResponse(link);
    }

    @DeleteMapping
    public LinkResponse removeLink(
        @RequestHeader(Headers.TG_CHAT_ID) Long tgChatId,
        @RequestBody @Valid RemoveLinkRequest requestBody
    ) {
        if (!bucket.tryConsume(1)) {
            throw new TooManyRequestsException();
        }
        log.debug("Removing link {} to id {}", requestBody, tgChatId);
        Link link = linksTransformService.toLink(requestBody);
        link = linkService.removeLink(tgChatId, link.getUrl());
        return linksTransformService.toLinkResponse(link);
    }

}
