package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.constants.Constants;
import java.util.function.BiPredicate;

public enum DefaultCommandTriggers {

    SIMPLE_COMMAND_TRIGGER((message, commandName) ->
        message.text().equals(Constants.COMMAND_TRIGGER + commandName)),
    COMMAND_WITH_ARGUMENTS_TRIGGER((message, commandName) ->
        message.text().split(" ")[0].equals(Constants.COMMAND_TRIGGER + commandName)),
    NOT_A_COMMAND_TRIGGER(
        (message, s) -> !message.text().startsWith(Constants.COMMAND_TRIGGER)
    );

    private final BiPredicate<Message, String> trigger;

    DefaultCommandTriggers(BiPredicate<Message, String> trigger) {
        this.trigger = trigger;
    }

    public boolean test(Message message, String commandName) {
        return trigger.test(message, commandName);
    }

}
