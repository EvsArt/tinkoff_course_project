package edu.java.bot.api.controller;

import edu.java.bot.api.dto.LinkUpdateRequest;
import edu.java.bot.api.exceptions.InvalidUpdateException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/updates")
public class UpdatesController implements IUpdatesController {

    @Autowired
    public UpdatesController() {

    }

    @PostMapping
    public void postUpdate(
        @RequestBody @Valid LinkUpdateRequest update,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new InvalidUpdateException("Invalid update!");
        }

        log.debug(String.format("Update %s was accepted", update));
    }

}
