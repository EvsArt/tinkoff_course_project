package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.constants.Constants;
import edu.java.bot.constants.StringService;
import edu.java.bot.messageProcessor.MessageParser;
import edu.java.bot.tracks.TemporaryTracksRepository;
import edu.java.bot.tracks.Track;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UntrackCommand implements Command {

    private final MessageParser messageParser;
    private final TemporaryTracksRepository tracksRepository;

    @Getter
    private final String name = StringService.COMMAND_UNTRACK_NAME;
    @Getter
    private final String description = StringService.COMMAND_UNTRACK_DESCRIPTION;

    @Autowired
    public UntrackCommand(
        MessageParser messageParser,
        TemporaryTracksRepository tracksRepository
    ) {
        this.messageParser = messageParser;
        this.tracksRepository = tracksRepository;
    }

    @Override
    public String getHelpMessage() {
        return StringService.COMMAND_UNTRACK_HELPMESSAGE;
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
    public SendMessage handle(Update update) {
        List<String> arguments = messageParser.parse(update.message()).arguments();

        // Illegal arguments count
        if (arguments.isEmpty() || arguments.size() > StringService.COMMAND_TRACK_ARGUMENTS_TO_DESCRIPTION.size()) {
            return new SendMessage(
                update.message().chat().id(),
                StringService.commandNeedHelp(this)
            );
        }

        // Tracking and untracking realizations will be changed in future updates :)
        User user = update.message().from();
        Optional<Track> trackOpt = tracksRepository.getTrackByName(user, arguments.get(0));

        SendMessage responseMessage = new SendMessage(update.message().chat().id(), "");
        trackOpt.ifPresentOrElse(
            track -> {
                tracksRepository.removeTrack(user, track);
                log.info("End tracking {}", track);
                responseMessage.getParameters()
                    .put(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE, StringService.endTracking(track));
            },
            () -> responseMessage.getParameters()
                .put(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE, StringService.COMMAND_UNTRACK_LINK_NOT_TRACKED)
        );

        return responseMessage;
    }
}
