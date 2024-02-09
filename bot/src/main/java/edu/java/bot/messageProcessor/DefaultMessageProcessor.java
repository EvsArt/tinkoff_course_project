package edu.java.bot.messageProcessor;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import edu.java.bot.exceptions.NoSuchCommandException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DefaultMessageProcessor implements MessageProcessor {

    private final List<Command> commands = new ArrayList<>();
    private final MessageParser messageParser;

    @Autowired
    public DefaultMessageProcessor(List<Command> commands, MessageParser messageParser) {
        this.commands.addAll(commands);
        this.messageParser = messageParser;
    }

    @Override
    public SendMessage process(Update update) {
        MessageParser.ParsedMessage message;
        try {
            message = messageParser.parse(update.message());
            log.info(message.toString());

            return commands.stream()
                .filter(command -> command.getName().equals(message.command()))
                .filter(command -> command.isAvailable(update.message().from()))
                .map(command -> command.handle(update))
                .findFirst()
                .orElseThrow(() -> new NoSuchCommandException(String.format(
                    "Command %s not exists!",
                    message.command()
                )));
        } catch (NoSuchCommandException e) {
            return new SendMessage(update.message().chat().id(), e.getMessage());
        }
    }

    @Override
    public List<Command> getCommands() {
        return commands;
    }

}
