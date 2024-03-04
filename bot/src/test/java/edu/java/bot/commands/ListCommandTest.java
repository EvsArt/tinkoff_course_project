package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.constants.Constants;
import edu.java.bot.constants.StringService;
import edu.java.bot.tracks.TemporaryTracksRepository;
import edu.java.bot.tracks.Track;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class ListCommandTest {

    @InjectMocks ListCommand listCommand;
    @Mock TemporaryTracksRepository tracksRepository;
    String command = Constants.COMMAND_TRIGGER + StringService.COMMAND_LIST_NAME;

    @Test
    void standardCalling() {
        Update update = getTestUpdateMessage();
        User user = Mockito.mock(User.class);
        Mockito.when(update.message().from()).thenReturn(user);
        Set<Track> tracks = new HashSet<>();
        tracks.add(new Track("www.eee.com/awsd/ad/1", "Track1"));
        tracks.add(new Track("www.eee.com/awsd/ad/2", "Track2"));
        Mockito.when(tracksRepository.isRegister(user)).thenReturn(true);
        Mockito.when(tracksRepository.getTracksByUser(user)).thenReturn(tracks);

        String expResult = StringService.tracksToPrettyView(tracks);

        SendMessage realResponse = listCommand.handle(update);
        String realResult = (String) realResponse.getParameters().get(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE);

        assertThat(realResult).isEqualTo(expResult);
    }

    @Test
    void withoutRegistrationCalling() {
        Update update = getTestUpdateMessage();
        User user = Mockito.mock(User.class);
        Mockito.when(update.message().from()).thenReturn(user);
        Mockito.when(tracksRepository.isRegister(update.message().from())).thenReturn(false);

        String expResult = StringService.PLEASE_REGISTER;

        SendMessage realResponse = listCommand.handle(update);
        String realResult = (String) realResponse.getParameters().get(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE);

        assertThat(realResult).isEqualTo(expResult);
    }

    @Test
    void emptyListCalling() {
        Update update = getTestUpdateMessage();
        User user = Mockito.mock(User.class);
        Mockito.when(update.message().from()).thenReturn(user);
        Mockito.when(tracksRepository.isRegister(user)).thenReturn(true);
        Mockito.when(tracksRepository.getTracksByUser(user)).thenReturn(Set.of());

        String expResult = StringService.tracksToPrettyView(Set.of());

        SendMessage realResponse = listCommand.handle(update);
        String realResult = (String) realResponse.getParameters().get(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE);

        assertThat(realResult).isEqualTo(expResult);
    }

    @Test
    void isTrigger() {
        Message mockMessage = Mockito.mock(Message.class);
        Mockito.doReturn(command).when(mockMessage).text();

        assertThat(listCommand.isTrigger(mockMessage)).isTrue();
    }

    @Test
    void isNotTrigger() {
        Message mockMessage = Mockito.mock(Message.class);
        Mockito.doReturn(command + "1234").when(mockMessage).text();

        assertThat(listCommand.isTrigger(mockMessage)).isFalse();
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
