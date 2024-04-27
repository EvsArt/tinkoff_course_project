package edu.java.bot.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.constants.StringService;
import edu.java.bot.dto.api.LinkUpdateRequest;
import edu.java.bot.metrics.ProcessedUpdate;
import edu.java.bot.tgBot.TgBot;
import org.springframework.stereotype.Service;

@Service
public class UpdatesService {

    private final TgBot bot;

    public UpdatesService(TgBot bot) {
        this.bot = bot;
    }

    @ProcessedUpdate
    public void sendUpdatesMessages(LinkUpdateRequest updateRequest) {
        updateRequest.getTgChatIds()
            .forEach(chatId -> bot.sendMessage(
                new SendMessage(
                    chatId,
                    StringService.receiveUpdate(updateRequest.getUrl(), updateRequest.getDescription())
                )
            ));
    }

}
