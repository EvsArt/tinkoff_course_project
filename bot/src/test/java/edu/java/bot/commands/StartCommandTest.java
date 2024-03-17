package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.constants.Constants;
import edu.java.bot.constants.StringService;
import edu.java.bot.tracks.TemporaryTracksRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class StartCommandTest {

    @InjectMocks StartCommand startCommand;

    @Mock TemporaryTracksRepository temporaryTracksRepository;
    String command = Constants.COMMAND_TRIGGER + StringService.COMMAND_START_NAME;

    @Test
    void shouldRegisterUserAndPrintResult() {
        Update update = getTestUpdateMessage();

        String expResult = StringService.COMMAND_START_SUCCESSFUL_REGISTRATION_MESSAGE;

        SendMessage realResponse = startCommand.handle(update);
        String realResult = (String) realResponse.getParameters().get(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE);

        Mockito.verify(temporaryTracksRepository, Mockito.only()).register(update.message().from());
        assertThat(realResult).isEqualTo(expResult);
    }

    @Test
    void isTrigger() {
        Message mockMessage = Mockito.mock(Message.class);
        Mockito.doReturn(command).when(mockMessage).text();

        assertThat(startCommand.isTrigger(mockMessage)).isTrue();
    }

    @Test
    void isNotTrigger() {
        Message mockMessage = Mockito.mock(Message.class);
        Mockito.doReturn(command + "1234").when(mockMessage).text();

        assertThat(startCommand.isTrigger(mockMessage)).isFalse();
    }

    private Update getTestUpdateMessage() {
        Update res = Mockito.mock(Update.class);
        Message message = Mockito.mock(Message.class);
        Chat chat = Mockito.mock(Chat.class);
        User user = Mockito.mock(User.class);

        Mockito.when(res.message()).thenReturn(message);
        Mockito.when(res.message().chat()).thenReturn(chat);
        Mockito.when(res.message().from()).thenReturn(user);
        Mockito.when(res.message().chat().id()).thenReturn(123L);
        return res;
    }

}
