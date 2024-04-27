package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.constants.Constants;
import edu.java.bot.constants.StringService;
import edu.java.bot.dto.scrapperClient.RemoveLinkRequest;
import edu.java.bot.exceptions.status.BadRequestException;
import edu.java.bot.exceptions.status.ResourceNotFoundException;
import edu.java.bot.exceptions.status.ServerErrorException;
import edu.java.bot.links.Link;
import edu.java.bot.links.service.LinksParsingService;
import edu.java.bot.messageProcessor.MessageParser;
import edu.java.bot.scrapperClient.client.ScrapperClient;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UntrackCommand implements Command {

    private final MessageParser messageParser;
    private final ScrapperClient scrapperClient;
    private final LinksParsingService linksParsingService;

    @Getter
    private final String name = StringService.COMMAND_UNTRACK_NAME;
    @Getter
    private final String description = StringService.COMMAND_UNTRACK_DESCRIPTION;

    @Autowired
    public UntrackCommand(
        MessageParser messageParser,
        ScrapperClient scrapperClient,
        LinksParsingService linksParsingService
    ) {
        this.messageParser = messageParser;
        this.scrapperClient = scrapperClient;
        this.linksParsingService = linksParsingService;
    }

    @Override
    public String getHelpMessage() {
        return StringService.COMMAND_UNTRACK_HELPMESSAGE;
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
        long chatId = update.message().chat().id();

        // Illegal arguments count
        if (arguments.isEmpty() || arguments.size() > StringService.COMMAND_TRACK_ARGUMENTS_TO_DESCRIPTION.size()) {
            return new SendMessage(
                chatId,
                StringService.commandNeedHelp(this)
            );
        }

        Link link = new Link(arguments.get(0), linksParsingService.getLinkName(arguments.get(0)));
        String url = arguments.get(0);

        SendMessage responseMessage = new SendMessage(chatId, "");
        try {
            scrapperClient.removeLink(chatId, new RemoveLinkRequest(url)).block();
            log.info("End tracking {} by chat {}", url, chatId);
            responseMessage.getParameters()
                .put(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE, StringService.endTracking(link));
        } catch (BadRequestException | ServerErrorException e) {
            return new SendMessage(update.message().chat().id(), StringService.errorWithUntrackLink(link));
        } catch (ResourceNotFoundException e) {
            return new SendMessage(chatId, StringService.linkNotExists(link));
        }

        return responseMessage;
    }
}
