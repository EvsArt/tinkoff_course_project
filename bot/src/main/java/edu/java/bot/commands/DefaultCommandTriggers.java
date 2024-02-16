package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.constants.StringService;
import java.util.function.BiPredicate;

public enum DefaultCommandTriggers {

    SIMPLE_COMMAND_TRIGGER((message, commandName) ->
        message.text().equals(StringService.COMMAND_TRIGGER + commandName)),
    COMMAND_WITH_ARGUMENTS_TRIGGER((message, commandName) ->
        message.text().startsWith(StringService.COMMAND_TRIGGER + commandName)),
    NOT_A_COMMAND_TRIGGER(
        (message, s) -> !message.text().startsWith(StringService.COMMAND_TRIGGER)
    );

    private final BiPredicate<Message, String> trigger;

    DefaultCommandTriggers(BiPredicate<Message, String> trigger) {
        this.trigger = trigger;
    }

    public boolean test(Message message, String commandName) {
        return trigger.test(message, commandName);
    }

}
