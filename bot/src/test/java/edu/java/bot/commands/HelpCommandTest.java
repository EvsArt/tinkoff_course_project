package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
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

@ExtendWith({MockitoExtension.class})
class HelpCommandTest {

    @InjectMocks HelpCommand helpCommand;

    @Mock CommandsHolder commandsHolder;

    String command = Constants.COMMAND_TRIGGER + StringService.COMMAND_HELP_NAME;

    @Test
    void isTrigger() {
        Message mockMessage = Mockito.mock(Message.class);
        Mockito.doReturn(command).when(mockMessage).text();

        assertThat(helpCommand.isTrigger(mockMessage)).isTrue();
    }

    @Test
    void isNotTrigger() {
        Message mockMessage = Mockito.mock(Message.class);
        Mockito.doReturn(command + "1234").when(mockMessage).text();

        assertThat(helpCommand.isTrigger(mockMessage)).isFalse();
    }

    @Test
    void shouldPrintCommandsList() {
        Update update = getTestUpdateMessage();

        Command availableCommand1 = Mockito.mock(Command.class);
        Mockito.when(availableCommand1.showInHelpList(update.message().from())).thenReturn(true);

        Command availableCommand2 = Mockito.mock(Command.class);
        Mockito.when(availableCommand2.showInHelpList(update.message().from())).thenReturn(true);

        Command notAvailableCommand = Mockito.mock(Command.class);
        Mockito.when(notAvailableCommand.showInHelpList(update.message().from())).thenReturn(false);

        List<Command> commandList = new ArrayList<>(List.of(availableCommand1, availableCommand2, notAvailableCommand));
        Mockito.when(commandsHolder.getCommands()).thenReturn(commandList);

        String expResult = StringService.availableCommandsToPrettyView(List.of(availableCommand1, availableCommand2));

        SendMessage realResponse = helpCommand.handle(update);
        String realResult = (String) realResponse.getParameters().get(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE);

        assertThat(realResult).isEqualTo(expResult);

    }

    private Update getTestUpdateMessage() {
        Update res = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Chat chat = Mockito.mock(Chat.class);

        Mockito.when(res.message()).thenReturn(message);
        Mockito.when(res.message().chat()).thenReturn(chat);
        Mockito.when(res.message().chat().id()).thenReturn(123L);
        return res;
    }

}
