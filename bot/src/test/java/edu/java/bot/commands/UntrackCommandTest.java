package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.constants.Constants;
import edu.java.bot.constants.StringService;
import edu.java.bot.messageProcessor.MessageParser;
import edu.java.bot.scrapperClient.client.ScrapperClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class UntrackCommandTest {

    private final String command = Constants.COMMAND_TRIGGER + StringService.COMMAND_UNTRACK_NAME;

    @InjectMocks UntrackCommand untrackCommand;
    @Spy MessageParser messageParser;

    @Mock ScrapperClient scrapperClient;

    @Test
    void handleWithoutArgumentsItShouldPrintHelpMessage() {
        Update update = getTestUpdateMessageWithText(command);

        String expResult = StringService.commandNeedHelp(untrackCommand);

        SendMessage realResponse = untrackCommand.handle(update);
        String realResult = (String) realResponse.getParameters().get(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE);

        assertThat(realResult).isEqualTo(expResult);
    }

    @Test
    void isTrigger() {
        Message mockMessage = Mockito.mock(Message.class);
        Mockito.doReturn(command).when(mockMessage).text();

        assertThat(untrackCommand.isTrigger(mockMessage)).isTrue();
    }

    @Test
    void isNotTrigger() {
        Message mockMessage = Mockito.mock(Message.class);
        Mockito.doReturn(command + "1234").when(mockMessage).text();

        assertThat(untrackCommand.isTrigger(mockMessage)).isFalse();
    }

    private Update getTestUpdateMessageWithText(String text) {
        Update res = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Chat chat = Mockito.mock(Chat.class);

        Mockito.when(res.message()).thenReturn(message);
        Mockito.when(res.message().chat()).thenReturn(chat);
        Mockito.when(res.message().chat().id()).thenReturn(123L);
        Mockito.when(res.message().text()).thenReturn(text);
        return res;
    }

}
