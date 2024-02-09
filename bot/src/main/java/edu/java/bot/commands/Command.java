package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.function.BiPredicate;

public interface Command {
    String getName();

    String getDescription();

    SendMessage handle(Update update);

    /**
     * Check if it is command calling
     *
     * @param message potential calling message
     * @return true if message is caller of this command
     */
    boolean isTrigger(Message message);

    /**
     * Check if this command available for user
     *
     * @param user the user who is being checked
     * @return true if user has access to this command
     *     <p>
     *     Without overriding command is available for everyone
     */
    default boolean isAvailable(User user) {
        return true;
    }

    default boolean showInHelpList(User user) {
        return isAvailable(user);
    }

    BiPredicate<Message, String>
        SIMPLE_COMMAND_TRIGGER = (message, commandName) -> message.text().equals('/' + commandName);
    BiPredicate<Message, String>
        COMMAND_WITH_ARGUMENTS_TRIGGER = (message, commandName) -> message.text().startsWith('/' + commandName);
    BiPredicate<Message, String>
        NOT_A_COMMAND_TRIGGER = ((message, s) -> !message.text().startsWith("/"));

}
