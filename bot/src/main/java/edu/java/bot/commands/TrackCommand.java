package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.constants.StringService;
import edu.java.bot.links.Link;
import edu.java.bot.links.service.LinksParsingService;
import edu.java.bot.links.service.LinksTransformService;
import edu.java.bot.links.validator.LinkValidatorManager;
import edu.java.bot.messageProcessor.MessageParser;
import edu.java.bot.scrapperClient.client.ScrapperClient;
import edu.java.bot.exceptions.status.BadRequestException;
import edu.java.bot.exceptions.status.ServerErrorException;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrackCommand implements Command {

    private final MessageParser messageParser;
    private final ScrapperClient scrapperClient;
    private final LinkValidatorManager linkValidator;
    private final LinksParsingService linksParsingService;
    private final LinksTransformService transformService;

    @Getter
    private final String name = StringService.COMMAND_TRACK_NAME;
    @Getter
    private final String description = StringService.COMMAND_TRACK_DESCRIPTION;

    @Autowired
    public TrackCommand(
        MessageParser messageParser,
        ScrapperClient scrapperClient,
        LinkValidatorManager linkValidator,
        LinksParsingService linksParsingService,
        LinksTransformService transformService
    ) {
        this.messageParser = messageParser;
        this.scrapperClient = scrapperClient;
        this.linkValidator = linkValidator;
        this.linksParsingService = linksParsingService;
        this.transformService = transformService;
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
        long chatId = update.message().chat().id();
        // Illegal arguments count
        if (arguments.isEmpty() || arguments.size() > StringService.COMMAND_TRACK_ARGUMENTS_TO_DESCRIPTION.size()) {
            return new SendMessage(
                chatId,
                StringService.commandNeedHelp(this)
            );
        }
        // Link not valid
        if (!linkValidator.validateLink(arguments.get(0))) {
            return new SendMessage(
                chatId,
                StringService.invalidTrackingLink(linkValidator.getAvailableServices())
            );
        }

        String link = arguments.get(0);
        String linkName;
        // Only url without name
        if (arguments.size() < 2) {
            linkName = linksParsingService.getLinkName(link);
        } else {
            linkName = arguments.get(1);
        }

        Link newLink = new Link(link, linkName);
        try {
            scrapperClient.addLink(chatId, transformService.toAddLinkRequest(newLink)).block();
        } catch (BadRequestException | ServerErrorException e) {
            return new SendMessage(chatId, StringService.errorWithTrackLink(newLink));
        }
        log.info("Chat {} starts tracking {}", chatId, newLink);

        return new SendMessage(update.message().chat().id(), StringService.startTracking(newLink));
    }
}
