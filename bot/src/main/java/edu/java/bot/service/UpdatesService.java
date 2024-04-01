package edu.java.bot.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.dto.LinkUpdateRequest;
import edu.java.bot.bot.TgBot;
import edu.java.bot.constants.StringService;
import org.springframework.stereotype.Service;

@Service
public class UpdatesService {

    private final TgBot bot;

    public UpdatesService(TgBot bot) {
        this.bot = bot;
    }

    public void sendUpdatesMessages(LinkUpdateRequest updateRequest) {
        updateRequest.getTgChatIds()
            .forEach(chatId -> bot.sendMessage(
                new SendMessage(chatId, StringService.receiveUpdate(updateRequest.getUrl(), updateRequest.getDescription()))
            ));
    }

}
