package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.constants.StringService;
import edu.java.bot.scrapperClient.client.ScrapperClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartCommand implements Command {

    private final ScrapperClient scrapperClient;

    @Getter
    private final String name = StringService.COMMAND_START_NAME;
    @Getter
    private final String description = StringService.COMMAND_START_DESCRIPTION;

    public StartCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    @Override
    public String getHelpMessage() {
        return StringService.COMMAND_START_HELPMESSAGE;
    }

    @Override
    public boolean isTrigger(Message message) {
        return DefaultCommandTriggers.SIMPLE_COMMAND_TRIGGER.test(message, getName());
    }

    @Override
    public boolean showInMyCommandsTable() {
        return false;
    }

    @Override
    public SendMessage handle(Update update) {
        log.info("Chat registration: {}", update.message().chat().id());

        scrapperClient.registerChat(update.message().chat().id()).block();

        return new SendMessage(
            update.message().chat().id(),
            StringService.COMMAND_START_SUCCESSFUL_REGISTRATION_MESSAGE
        );
    }
}
