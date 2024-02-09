package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartCommand implements Command {

    @Getter
    private final String name = "start";
    @Getter
    private final String description = "Register user";

    @Override
    public SendMessage handle(Update update) {
        log.info("User registration: {}", update.message().from());

        // TODO: 10.02.2024 Realize user registration

        return new SendMessage(
            update.message().chat().id(),
            "You was registered!"
        );
    }

    @Override
    public boolean isTrigger(Message message) {
        return Command.SIMPLE_COMMAND_TRIGGER.test(message, getName());
    }
}
