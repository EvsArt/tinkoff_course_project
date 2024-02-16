package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.constants.StringService;
import edu.java.bot.tracks.TemporaryTracksRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ListCommand implements Command {

    private final TemporaryTracksRepository tracksRepository;

    @Getter
    private final String name = StringService.COMMAND_LIST_NAME;
    @Getter
    private final String description = StringService.COMMAND_LIST_DESCRIPTION;

    @Autowired
    public ListCommand(TemporaryTracksRepository tracksRepository) {
        this.tracksRepository = tracksRepository;
    }

    @Override
    public String getHelpMessage() {
        return StringService.COMMAND_LIST_HELPMESSAGE;
    }

    @Override
    public SendMessage handle(Update update) {
        log.debug("Command {}{} was called", StringService.COMMAND_TRIGGER, name);
        if (!tracksRepository.isRegister(update.message().from())) {
            return new SendMessage(
                update.message().chat().id(), StringService.PLEASE_REGISTER
            );
        }
        return new SendMessage(
            update.message().chat().id(),
            StringService.tracksToPrettyView(tracksRepository.getTracksByUser(update.message().from()))
        );
    }

    @Override
    public boolean isAvailable(User user) {
        return tracksRepository.isRegister(user);
    }

    @Override
    public boolean isTrigger(Message message) {
        return DefaultCommandTriggers.COMMAND_WITH_ARGUMENTS_TRIGGER.test(message, getName());
    }

}
