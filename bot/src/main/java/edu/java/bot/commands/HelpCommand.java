package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.constants.StringService;
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
    private final String name = StringService.COMMAND_HELP_NAME;
    @Getter
    private final String description = StringService.COMMAND_HELP_DESCRIPTION;

    private final ApplicationContext context;

    @Autowired
    public HelpCommand(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public boolean isTrigger(Message message) {
        return DefaultCommandTriggers.SIMPLE_COMMAND_TRIGGER.test(message, name);
    }

    @Override
    public String getHelpMessage() {
        return StringService.COMMAND_HELP_HELPMESSAGE;
    }

    @Override
    public SendMessage handle(Update update) {
        return new SendMessage(
            update.message().chat().id(),
            StringService.availableCommandsToPrettyView(getAvailableCommands(update.message().from()))
        );
    }

    private List<Command> getAvailableCommands(User user) {
        return context.getBean(CommandsHolder.class).getCommands().stream()
            .filter(it -> it.showInHelpList(user))
            .toList();
    }

}
