package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.constants.StringService;
import edu.java.bot.messageProcessor.MessageParser;
import edu.java.bot.tracks.TemporaryTracksRepository;
import edu.java.bot.tracks.Track;
import edu.java.bot.tracks.validator.LinkValidatorManager;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrackCommand implements Command {

    private final MessageParser messageParser;
    private final TemporaryTracksRepository tracksRepository;
    private final LinkValidatorManager linkValidator;

    @Getter
    private final String name = StringService.COMMAND_TRACK_NAME;
    @Getter
    private final String description = StringService.COMMAND_TRACK_DESCRIPTION;

    @Autowired
    public TrackCommand(
        MessageParser messageParser,
        TemporaryTracksRepository tracksRepository,
        LinkValidatorManager linkValidator
    ) {
        this.messageParser = messageParser;
        this.tracksRepository = tracksRepository;
        this.linkValidator = linkValidator;
    }

    @Override
    public boolean isAvailable(User user) {
        return tracksRepository.isRegister(user);
    }

    @Override
    public boolean isTrigger(Message message) {
        return DefaultCommandTriggers.COMMAND_WITH_ARGUMENTS_TRIGGER.test(message, getName());
    }

    @Override
    public boolean showInMyCommandsTable() {
        return false;
    }

    @Override
    public String getHelpMessage() {
        return StringService.COMMAND_TRACK_HELPMESSAGE;
    }

    @Override
    public SendMessage handle(Update update) {
        List<String> arguments = messageParser.parse(update.message()).arguments();
        // Illegal arguments count
        if (arguments.isEmpty() || arguments.size() > StringService.COMMAND_TRACK_ARGUMENTS_TO_DESCRIPTION.size()) {
            return new SendMessage(
                update.message().chat().id(),
                StringService.commandNeedHelp(this)
            );
        }
        // Link not valid
        if (!linkValidator.validateLink(arguments.get(0))) {
            return new SendMessage(
                update.message().chat().id(),
                StringService.invalidTrackingLink(linkValidator.getAvailableServices())
            );
        }

        String link = arguments.get(0);
        String linkName;
        // Only link without name
        if (arguments.size() < 2) {
            linkName = tracksRepository.getNewTrackNameFor(update.message().from());
        } else {
            linkName = arguments.get(1);
        }

        // Tracking and untracking realizations will be changed in future updates :)
        User user = update.message().from();
        Track newTrack = new Track(link, linkName);
        tracksRepository.addTrack(user, newTrack);
        log.info("User {} starts tracking {}", user, newTrack);

        return new SendMessage(update.message().chat().id(), StringService.startTracking(newTrack));
    }
}
