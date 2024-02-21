package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.constants.StringService;
import edu.java.bot.tracks.TemporaryTracksRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartCommand implements Command {

    private final TemporaryTracksRepository tracksRepository;

    @Getter
    private final String name = StringService.COMMAND_START_NAME;
    @Getter
    private final String description = StringService.COMMAND_START_DESCRIPTION;

    public StartCommand(TemporaryTracksRepository tracksRepository) {
        this.tracksRepository = tracksRepository;
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
        log.info("User registration: {}", update.message().from());

        tracksRepository.register(update.message().from());

        return new SendMessage(
            update.message().chat().id(),
            StringService.COMMAND_START_SUCCESSFUL_REGISTRATION_MESSAGE
        );
    }
}
