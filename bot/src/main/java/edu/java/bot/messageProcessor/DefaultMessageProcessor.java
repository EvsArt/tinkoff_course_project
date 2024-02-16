package edu.java.bot.messageProcessor;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.CommandsHolder;
import edu.java.bot.constants.StringService;
import edu.java.bot.exceptions.NoSuchCommandException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DefaultMessageProcessor implements MessageProcessor {

    private final CommandsHolder commandsHolder;
    private final MessageParser messageParser;

    @Autowired
    public DefaultMessageProcessor(CommandsHolder commandsHolder, MessageParser messageParser) {
        this.commandsHolder = commandsHolder;
        this.messageParser = messageParser;
    }

    @Override
    public SendMessage process(Update update) {
        MessageParser.ParsedMessage message;
        try {
            message = messageParser.parse(update.message());
            log.debug("Received request: {}", message.toString());

            Command foundCommand = getCommands().stream()
                .filter(command -> command.isAvailable(update.message().from()))    // command is available for user
                .filter(command -> command.isTrigger(update.message()))             // command exists
                .findFirst()
                .orElseThrow(() -> new NoSuchCommandException(StringService.commandNotSupports(message.command())));

            // user requested help with command
            if (!message.arguments().isEmpty()
                && message.arguments().getFirst().equals(StringService.COMMAND_NEED_HELP_ARGUMENT)) {
                return new SendMessage(update.message().chat().id(), foundCommand.getHelpMessage());
            }

            return foundCommand.handle(update);

        } catch (NoSuchCommandException e) {
            return new SendMessage(update.message().chat().id(), e.getMessage());
        }
    }

    @Override
    public List<Command> getCommands() {
        return commandsHolder.getCommands();
    }

}
