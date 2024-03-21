package edu.java.bot.commands;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AllCommandsHolder implements CommandsHolder {

    private final List<Command> commands;

    public AllCommandsHolder(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public List<Command> getCommands() {
        return commands;
    }

}
