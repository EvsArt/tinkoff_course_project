package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.constants.StringService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotACommand implements Command {

    @Getter
    private final String name = StringService.COMMAND_NOT_A_COMMAND_NAME;

    @Getter
    private final String description = StringService.COMMAND_NOT_A_COMMAND_DESCRIPTION;

    @Override
    public String getHelpMessage() {
        return "";
    }

    @Override
    public boolean showInMyCommandsTable() {
        return false;
    }

    @Override
    public SendMessage handle(Update update) {
        log.debug("{} was called", getName());
        return new SendMessage(update.message().chat().id(), StringService.COMMAND_NOT_A_COMMAND_NOT_SUPPORTS_MESSAGE);
    }

    @Override
    public boolean isTrigger(Message message) {
        return DefaultCommandTriggers.NOT_A_COMMAND_TRIGGER.test(message, getName());
    }

    @Override
    public boolean showInHelpList(User user) {
        return false;
    }

}
