package edu.java.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.java.botClient.BotClient;
import edu.java.botClient.dto.LinkUpdateRequest;
import edu.java.model.LinkUpdateInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendUpdatesServiceImpl implements SendUpdatesService {

    private final BotClient botClient;

    @Autowired
    public SendUpdatesServiceImpl(BotClient botClient) {
        this.botClient = botClient;
    }

    @Override
    public void sendUpdate(LinkUpdateInfo updateInfo) {
        LinkUpdateRequest request = LinkUpdateRequest.builder()
            .url(updateInfo.getUrl())
            .description(updateInfo.getMessage())
            .tgChatIds(updateInfo.getTgChatsIds())
            .build();
        try {
            botClient.postUpdates(request).block();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
