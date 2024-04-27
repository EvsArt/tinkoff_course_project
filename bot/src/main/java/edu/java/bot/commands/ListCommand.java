package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.constants.Constants;
import edu.java.bot.constants.StringService;
import edu.java.bot.links.Link;
import edu.java.bot.links.service.LinksParsingService;
import edu.java.bot.links.service.LinksTransformService;
import edu.java.bot.scrapperClient.client.ScrapperClient;
import edu.java.bot.exceptions.status.BadRequestException;
import edu.java.bot.exceptions.status.ServerErrorException;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ListCommand implements Command {

    private final ScrapperClient scrapperClient;
    private final LinksParsingService parsingService;
    private final LinksTransformService transformService;

    @Getter
    private final String name = StringService.COMMAND_LIST_NAME;
    @Getter
    private final String description = StringService.COMMAND_LIST_DESCRIPTION;

    @Autowired
    public ListCommand(
        ScrapperClient scrapperClient, LinksParsingService parsingService,
        LinksTransformService transformService
    ) {
        this.scrapperClient = scrapperClient;
        this.parsingService = parsingService;
        this.transformService = transformService;
    }

    @Override
    public String getHelpMessage() {
        return StringService.COMMAND_LIST_HELPMESSAGE;
    }

    @Override
    public SendMessage handle(Update update) {
        log.debug("Command {}{} was called", Constants.COMMAND_TRIGGER, name);

        long chatId = update.message().chat().id();

        Set<Link> links;
        try {
            links = scrapperClient.getLinks(chatId).block().getLinks().stream()
                .map(transformService::toLink)
                .collect(Collectors.toSet());
        } catch (BadRequestException | ServerErrorException e) {
            return new SendMessage(chatId, StringService.errorWithGettingLinks());
        }

        return new SendMessage(
            chatId,
            StringService.tracksToPrettyView(links)
        );
    }

    @Override
    public boolean isTrigger(Message message) {
        return DefaultCommandTriggers.COMMAND_WITH_ARGUMENTS_TRIGGER.test(message, getName());
    }

}
