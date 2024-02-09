package edu.java.bot.messageProcessor;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.exceptions.NoSuchCommandException;
import org.springframework.stereotype.Service;

@Service
public class MessageParser {

    public ParsedMessage parse(Message message) throws NoSuchCommandException {

        boolean isCommand = message.text().startsWith("/");

        if (isCommand) {
            String[] commandArray = message.text().split(" ", 2);
            String command = commandArray[0].replaceFirst("/", "");
            String arguments = "";
            if (commandArray.length == 2) {
                arguments = commandArray[1];
            }

            return new ParsedMessage(true, command, arguments);
        }

        return new ParsedMessage(false, "not-a-command", message.text());

    }

    public record ParsedMessage(boolean isCommand, String command, String arguments) {
    }

}
