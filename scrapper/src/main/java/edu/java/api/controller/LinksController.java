package edu.java.api.controller;

import edu.java.api.constants.Headers;
import edu.java.api.dto.AddLinkRequest;
import edu.java.api.dto.LinkResponse;
import edu.java.api.dto.ListLinksResponse;
import edu.java.api.dto.RemoveLinkRequest;
import edu.java.api.exceptions.WrongRequestFormatException;
import edu.java.api.service.LinksService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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

    private final LinksService linksService;

    public LinksController(LinksService linksService) {
        this.linksService = linksService;
    }

    @GetMapping
    public ResponseEntity<ListLinksResponse> getLinks(@RequestHeader(Headers.TG_CHAT_ID) Long tgChatId) {
        log.debug("Getting links by id {}", tgChatId);
        return ResponseEntity.ok(
            linksService.getListLinksResponseByTgChatId(tgChatId)
        );
    }

    @PostMapping
    public ResponseEntity<LinkResponse> addLink(
        @RequestHeader(Headers.TG_CHAT_ID) Long tgChatId,
        @RequestBody @Valid AddLinkRequest requestBody,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new WrongRequestFormatException();
        }

        log.debug("Adding link {} to id {}", requestBody, tgChatId);
        return ResponseEntity.ok(
            linksService.saveLink(tgChatId, requestBody)
        );
    }

    @DeleteMapping
    public ResponseEntity<LinkResponse> removeLink(
        @RequestHeader(Headers.TG_CHAT_ID) Long tgChatId,
        @RequestBody @Valid RemoveLinkRequest requestBody,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new WrongRequestFormatException();
        }

        log.debug("Removing link {} to id {}", requestBody, tgChatId);
        return ResponseEntity.ok(
            linksService.removeLink(tgChatId, requestBody)
        );
    }

}
