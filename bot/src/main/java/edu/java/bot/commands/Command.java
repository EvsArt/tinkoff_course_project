package edu.java.bot.commands;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;

/**
 * You just need to create a bean implements Command
 * for creating new command in bot
 */
public interface Command {
    String getName();

    String getDescription();

    String getHelpMessage();

    SendMessage handle(Update update);

    /**
     * Check if it is command calling
     *
     * @param message potential calling message
     * @return true if message is caller of this command
     *     <p>
     *     You can use default triggers
     * @see DefaultCommandTriggers
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

    /**
     * Check if this command should to be shown for user in help list
     *
     * @param user user who want to see commands
     * @return true if command should to be shown for user else false
     *     <p>
     *     Without overriding command shows if it is available
     *     </p>
     */
    default boolean showInHelpList(User user) {
        return isAvailable(user);
    }

    default boolean showInMyCommandsTable() {
        return true;
    }

    default BotCommand toApiCommand() {
        return new BotCommand(getName(), getDescription());
    }

}
