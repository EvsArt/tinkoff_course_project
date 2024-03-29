package edu.java.bot.api.controller;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.dto.LinkUpdateRequest;
import edu.java.bot.bot.TgBot;
import edu.java.bot.constants.StringService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/updates")
public class UpdatesController implements IUpdatesController {

    private final TgBot bot;

    @Autowired
    public UpdatesController(TgBot bot) {
        this.bot = bot;
    }

    @PostMapping
    public void postUpdate(
        @RequestBody @Valid LinkUpdateRequest update
    ) {
        log.debug(String.format("Update %s was accepted", update));
        update.getTgChatIds()
            .forEach(chatId -> bot.sendMessage(
                new SendMessage(chatId, StringService.receiveUpdate(update.getUrl(), update.getDescription()))
            ));
    }

}
