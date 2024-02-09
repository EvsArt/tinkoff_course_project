package edu.java.bot.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.messageProcessor.MessageProcessor;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyTgBot implements TgBot {

    private final ApplicationConfig applicationConfig;
    private final MessageProcessor messageProcessor;
    private TelegramBot bot;

    @Autowired
    public MyTgBot(
        ApplicationConfig applicationConfig,
        MessageProcessor messageProcessor
    ) {
        this.applicationConfig = applicationConfig;
        this.messageProcessor = messageProcessor;
        this.start();
    }

    @Override
    public void start() {
        bot = new TelegramBot(applicationConfig.telegramToken());
        bot.setUpdatesListener(this);
    }

    @Override
    public void close() throws Exception {
        bot.shutdown();
        log.info("Bot was closed");
    }

    @Override
    public int process(List<Update> updates) {
        log.debug("{} updates was received: {}", updates.size(), updates);

        List<SendMessage> responses = updates.stream()
            .map(messageProcessor::process)
            .toList();
        responses.forEach(bot::execute);

        log.debug("Executed responses: " + responses.stream()
            .map(BaseRequest::getParameters)
            .toList()
        );

        return CONFIRMED_UPDATES_ALL;
    }
}
