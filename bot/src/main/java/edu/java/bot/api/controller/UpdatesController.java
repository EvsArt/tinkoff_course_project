package edu.java.bot.api.controller;

import edu.java.bot.api.dto.LinkUpdateRequest;
import edu.java.bot.metrics.ReceivedHttpUpdate;
import edu.java.bot.scrapperClient.exceptions.status.TooManyRequestsException;
import edu.java.bot.service.UpdatesService;
import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/updates")
public class UpdatesController implements IUpdatesController {

    private final UpdatesService updatesService;
    private final Bucket bucket;

    @Autowired
    public UpdatesController(UpdatesService updatesService, @Qualifier("updatesRateLimitBucket") Bucket bucket) {
        this.updatesService = updatesService;
        this.bucket = bucket;
    }

    @PostMapping
    @ReceivedHttpUpdate
    public void postUpdate(
        @RequestBody @Valid LinkUpdateRequest update
    ) {
        if (!bucket.tryConsume(1)) {
            throw new TooManyRequestsException();
        }
        log.debug(String.format("Update %s was accepted", update));
        updatesService.sendUpdatesMessages(update);
    }

}
