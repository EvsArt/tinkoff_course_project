package edu.java.bot.tgBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.messageProcessor.MessageProcessor;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyTgBot implements TgBot {

    private final ApplicationConfig applicationConfig;
    private final MessageProcessor messageProcessor;
    private final SetMyCommands setMyCommands;
    private TelegramBot botCore;

    @Autowired
    public MyTgBot(
        ApplicationConfig applicationConfig,
        MessageProcessor messageProcessor,
        SetMyCommands setMyCommands
    ) {
        this.applicationConfig = applicationConfig;
        this.messageProcessor = messageProcessor;
        this.setMyCommands = setMyCommands;
    }

    @Override
    @EventListener(classes = ContextRefreshedEvent.class)
    public void start() {
        // Spring actuator creates new context and ContextRefreshedEvent is published twice
        if (botCore != null) {
            log.warn("Bot has already started so new bot creation was skipped");
            return;
        }
        botCore = new TelegramBot(applicationConfig.telegramToken());
        botCore.setUpdatesListener(this, this);

        botCore.execute(setMyCommands);

        log.info("Bot was started");
    }

    @Override
    public void close() {
        botCore.shutdown();
        log.info("Bot was closed");
    }

    @Override
    public int process(List<Update> updates) {
        log.debug("{} updates was received: {}", updates.size(), updates);

        List<SendMessage> responses = updates.stream()
            .filter(update -> update.message() != null)
            .filter(update -> update.message().text() != null)
            .map(messageProcessor::process)
            .toList();
        responses.forEach(botCore::execute);

        log.debug("Executed responses: " + responses.stream()
            .map(BaseRequest::getParameters)
            .toList()
        );

        return CONFIRMED_UPDATES_ALL;
    }

    @Override
    @Async
    public void sendMessage(SendMessage message) {
        botCore.execute(message);
    }

    @Override
    public void onException(TelegramException e) {
        if (e.response() != null) {
            // got bad response from telegram
            log.error(
                "Update handling exception: {code: {}, description:{}}",
                e.response().errorCode(),
                e.response().description()
            );
        } else {
            // probably network error
            log.error(e.getMessage());
        }
    }

}
