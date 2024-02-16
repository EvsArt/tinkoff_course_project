package edu.java.bot.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.CommandsHolder;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.messageProcessor.MessageProcessor;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyTgBot implements TgBot {

    private final ApplicationConfig applicationConfig;
    private final MessageProcessor messageProcessor;
    private final CommandsHolder commandsHolder;
    private TelegramBot botCore;

    @Autowired
    public MyTgBot(
        ApplicationConfig applicationConfig,
        MessageProcessor messageProcessor,
        CommandsHolder commandsHolder
    ) {
        this.applicationConfig = applicationConfig;
        this.messageProcessor = messageProcessor;
        this.commandsHolder = commandsHolder;
    }

    @Override
    @EventListener(classes = ContextRefreshedEvent.class)
    public void start() {

        botCore = new TelegramBot(applicationConfig.telegramToken());
        botCore.setUpdatesListener(this, this);

        setMyCommands();

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

    private void setMyCommands() {

        BotCommand[] allCommands = commandsHolder.getCommands().stream()
            .filter(Command::showInMyCommandsTable)
            .map(Command::toApiCommand)
            .toArray(BotCommand[]::new);

        SetMyCommands set = new SetMyCommands(allCommands);

        botCore.execute(set);

        log.debug("My commands set: {}", Arrays.toString((BotCommand[]) set.getParameters().get("commands")));
    }

}
