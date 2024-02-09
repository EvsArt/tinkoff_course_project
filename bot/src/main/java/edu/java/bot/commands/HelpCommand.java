package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelpCommand implements Command {

    @Getter
    private final String name = "help";
    @Getter
    private final String description = "Print list of available commands";

    private final ApplicationContext context;

    @Autowired
    public HelpCommand(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public boolean isTrigger(Message message) {
        return SIMPLE_COMMAND_TRIGGER.test(message, name);
    }

    @Override
    public SendMessage handle(Update update) {
        return new SendMessage(
            update.message().chat().id(),
            toPrettyView(getAvailableCommands(update.message().from()))
        );
    }

    private List<Command> getAvailableCommands(User user) {
        return context.getBeansOfType(Command.class).values().stream()
            .filter(it -> it.showInHelpList(user))
            .toList();
    }

    private String toPrettyView(List<Command> commands) {
        StringBuilder builder = new StringBuilder();
        builder.append("Available commands list:\n");
        commands.forEach(command -> builder.append(
            String.format("%c%s - %s\n", '/',
                command.getName(),
                command.getDescription()
            )
        ));

        return builder.toString();
    }

}
