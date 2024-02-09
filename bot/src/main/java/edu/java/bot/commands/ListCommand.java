package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.tracks.Track;
import edu.java.bot.tracks.TracksHolder;
import java.util.Set;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ListCommand implements Command {

    private final TracksHolder tracksHolder;

    @Getter
    private final String name = "list";
    @Getter
    private final String description = "Print tracking list";

    @Autowired
    public ListCommand(TracksHolder tracksHolder) {
        this.tracksHolder = tracksHolder;
    }

    @Override
    public SendMessage handle(Update update) {
        log.debug("/list was called");
        return new SendMessage(update.message().chat().id(), tracksToPrettyView(tracksHolder.getTracks()));
    }

    @Override
    public boolean isTrigger(Message message) {
        return COMMAND_WITH_ARGUMENTS_TRIGGER.test(message, getName());
    }

    private String tracksToPrettyView(Set<Track> tracks) {
        StringBuilder builder = new StringBuilder();
        builder.append("Your tracks:\n");
        tracks.forEach(track -> builder.append(String.format("-- %s\n", track.link())));
        return builder.toString();
    }

}
