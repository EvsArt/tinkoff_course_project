package edu.java.bot.bot;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.CommandsHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;

@Slf4j
@Configuration
public class BotConfig {

    private final CommandsHolder commandsHolder;

    @Autowired
    public BotConfig(CommandsHolder commandsHolder) {
        this.commandsHolder = commandsHolder;
    }

    @Bean
    public SetMyCommands setMyCommands() {
        BotCommand[] allCommands = commandsHolder.getCommands().stream()
            .filter(Command::showInMyCommandsTable)
            .map(Command::toApiCommand)
            .toArray(BotCommand[]::new);

        SetMyCommands set = new SetMyCommands(allCommands);

        log.debug("My commands set: {}", Arrays.toString((BotCommand[]) set.getParameters().get("commands")));

        return set;
    }

}
