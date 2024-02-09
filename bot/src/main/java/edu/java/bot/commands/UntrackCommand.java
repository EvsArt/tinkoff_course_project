package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.messageProcessor.MessageParser;
import edu.java.bot.tracks.Track;
import edu.java.bot.tracks.TracksHolder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UntrackCommand implements Command {

    private final MessageParser messageParser;
    private final TracksHolder tracksHolder;

    @Getter
    private final String name = "untrack";
    @Getter
    private final String description = "Stop link tracking (need 1 argument after link)";

    @Autowired
    public UntrackCommand(MessageParser messageParser, TracksHolder tracksHolder) {
        this.messageParser = messageParser;
        this.tracksHolder = tracksHolder;
    }

    @Override
    public SendMessage handle(Update update) {
        String argument = messageParser.parse(update.message()).arguments();

        if (argument.isBlank()) {
            return new SendMessage(update.message().chat().id(), "Argument requires!");
        }
        tracksHolder.removeTrack(new Track(argument));
        log.info("End tracking {}", argument);

        return new SendMessage(update.message().chat().id(), String.format("End tracking %s", argument));
    }

    @Override
    public boolean isTrigger(Message message) {
        return COMMAND_WITH_ARGUMENTS_TRIGGER.test(message, getName());
    }
}
