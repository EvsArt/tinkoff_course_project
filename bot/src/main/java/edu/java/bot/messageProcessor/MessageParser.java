package edu.java.bot.messageProcessor;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.constants.StringService;
import edu.java.bot.exceptions.NoSuchCommandException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MessageParser {

    public ParsedMessage parse(Message message) throws NoSuchCommandException {

        boolean isCommand = message.text().startsWith(StringService.COMMAND_TRIGGER);

        if (isCommand) {
            String[] commandAndArguments = message.text().split(" ", 2);
            String command = commandAndArguments[0].replaceFirst(StringService.COMMAND_TRIGGER, "");
            List<String> arguments = new ArrayList<>();
            if (commandAndArguments.length == 2) {
                arguments = Arrays.stream(commandAndArguments[1].split(" ")).toList();
            }

            return new ParsedMessage(true, command, arguments);
        }

        return new ParsedMessage(false, StringService.COMMAND_NOT_A_COMMAND_NAME, List.of(message.text()));

    }

    public record ParsedMessage(boolean isCommand, String command, List<String> arguments) {
    }

}
