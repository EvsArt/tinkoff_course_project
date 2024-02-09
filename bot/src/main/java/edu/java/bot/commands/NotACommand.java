package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class NotACommand implements Command {
    @Override
    public String getName() {
        return "not-a-command";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public SendMessage handle(Update update) {
        return new SendMessage(update.message().chat().id(), "Suddenly bot doesn't support not-command messages");
    }

    @Override
    public boolean isTrigger(Message message) {
        return NOT_A_COMMAND_TRIGGER.test(message, getName());
    }

    @Override
    public boolean showInHelpList(User user) {
        return false;
    }

}
