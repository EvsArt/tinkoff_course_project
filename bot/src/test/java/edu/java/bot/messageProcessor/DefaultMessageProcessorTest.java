package edu.java.bot.messageProcessor;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.CommandsHolder;
import edu.java.bot.constants.Constants;
import edu.java.bot.constants.StringService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class DefaultMessageProcessorTest {

    @InjectMocks DefaultMessageProcessor messageProcessor;
    @Mock MessageParser messageParser;
    @Mock CommandsHolder commandsHolder;

    @Test
    void callCommand() {
        Update update = getTestUpdateMessageWithText();
        MessageParser.ParsedMessage parsedMessage = new MessageParser.ParsedMessage(true, "help", List.of());
        Mockito.when(messageParser.parse(update.message())).thenReturn(parsedMessage);

        SendMessage expResult = new SendMessage(update.message().chat().id(), "Test text");

        List<Command> commands = new ArrayList<>();
        Command helpCommand = Mockito.mock(Command.class);
        Mockito.when(helpCommand.isAvailable(update.message().from())).thenReturn(true);
        Mockito.when(helpCommand.isTrigger(update.message())).thenReturn(true);
        Mockito.when(helpCommand.handle(update)).thenReturn(expResult);
        commands.add(helpCommand);

        Mockito.when(messageProcessor.getCommands()).thenReturn(commands);

        SendMessage realResult = messageProcessor.process(update);

        Mockito.verify(helpCommand, Mockito.times(1)).handle(update);
        Mockito.verify(helpCommand, Mockito.times(1)).isTrigger(update.message());
        Mockito.verify(helpCommand, Mockito.times(1)).isAvailable(update.message().from());
        assertThat(realResult).isEqualTo(expResult);
    }

    @Test
    void callNeedHelpCommand() {
        Update update = getTestUpdateMessageWithText();
        MessageParser.ParsedMessage parsedMessage =
            new MessageParser.ParsedMessage(true, "help", List.of(Constants.COMMAND_NEED_HELP_ARGUMENT));
        Mockito.when(messageParser.parse(update.message())).thenReturn(parsedMessage);

        String helpMessage = "It's a help message!";

        List<Command> commands = new ArrayList<>();
        Command helpCommand = Mockito.mock(Command.class);
        Mockito.when(helpCommand.isAvailable(update.message().from())).thenReturn(true);
        Mockito.when(helpCommand.isTrigger(update.message())).thenReturn(true);
        Mockito.when(helpCommand.getHelpMessage()).thenReturn(helpMessage);
        commands.add(helpCommand);

        Mockito.when(messageProcessor.getCommands()).thenReturn(commands);

        SendMessage realResponse = messageProcessor.process(update);
        String realResult = (String) realResponse.getParameters().get(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE);

        Mockito.verify(helpCommand, Mockito.never()).handle(update);
        Mockito.verify(helpCommand, Mockito.times(1)).isTrigger(update.message());
        Mockito.verify(helpCommand, Mockito.times(1)).isAvailable(update.message().from());
        assertThat(realResult).isEqualTo(helpMessage);
    }

    @Test
    void noSuchCommandTest() {
        Update update = getTestUpdateMessageWithText();
        String commandName = "help";
        MessageParser.ParsedMessage parsedMessage = new MessageParser.ParsedMessage(true, commandName, List.of());
        Mockito.when(messageParser.parse(update.message())).thenReturn(parsedMessage);

        String expResult = StringService.commandNotSupports(commandName);

        List<Command> commands = new ArrayList<>();
        Command helpCommand = Mockito.mock(Command.class);
        commands.add(helpCommand);
        Mockito.when(messageProcessor.getCommands()).thenReturn(commands);

        SendMessage realResponse = messageProcessor.process(update);
        String realResult = (String) realResponse.getParameters().get(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE);

        Mockito.verify(helpCommand, Mockito.never()).handle(update);
        assertThat(realResult).isEqualTo(expResult);
    }

    private Update getTestUpdateMessageWithText() {
        Update res = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Chat chat = Mockito.mock(Chat.class);

        Mockito.when(res.message()).thenReturn(message);
        Mockito.when(res.message().chat()).thenReturn(chat);
        Mockito.when(res.message().chat().id()).thenReturn(123L);
        return res;
    }

}
