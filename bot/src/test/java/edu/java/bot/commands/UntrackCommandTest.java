package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.constants.Constants;
import edu.java.bot.constants.StringService;
import edu.java.bot.messageProcessor.MessageParser;
import edu.java.bot.tracks.TemporaryTracksRepository;
import edu.java.bot.tracks.Track;
import java.util.Optional;
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

    @Mock TemporaryTracksRepository tracksRepository;

    @Test
    void handleWithoutArgumentsItShouldPrintHelpMessage() {
        Update update = getTestUpdateMessageWithText(command);

        String expResult = StringService.commandNeedHelp(untrackCommand);

        SendMessage realResponse = untrackCommand.handle(update);
        String realResult = (String) realResponse.getParameters().get(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE);

        assertThat(realResult).isEqualTo(expResult);
    }

    @Test
    void handleWithOneArgumentItShouldRemoveTrack() {
        String name = "Track1";

        Update update = getTestUpdateMessageWithText(String.format("%s %s", command, name));
        User user = Mockito.mock(User.class);
        Mockito.when(update.message().from()).thenReturn(user);
        Track track = new Track("someLink", name);
        Mockito.when(tracksRepository.getTrackByName(user, name)).thenReturn(Optional.of(track));

        String expResult = StringService.endTracking(track);

        SendMessage realResponse = untrackCommand.handle(update);
        String realResult = (String) realResponse.getParameters().get(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE);

        Mockito.verify(tracksRepository, Mockito.times(1)).removeTrack(update.message().from(), track);
        assertThat(realResult).isEqualTo(expResult);
    }

    @Test
    void handleWithUnknownNameShouldPrintMessage() {
        String name = "UnknownName";

        Update update = getTestUpdateMessageWithText(String.format("%s %s", command, name));
        User user = Mockito.mock(User.class);
        Mockito.when(update.message().from()).thenReturn(user);
        Mockito.when(tracksRepository.getTrackByName(user, name)).thenReturn(Optional.empty());

        String expResult = StringService.COMMAND_UNTRACK_LINK_NOT_TRACKED;

        SendMessage realResponse = untrackCommand.handle(update);
        String realResult = (String) realResponse.getParameters().get(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE);

        Mockito.verify(tracksRepository, Mockito.never()).removeTrack(Mockito.any(), Mockito.any());
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
        Mockito.doReturn(command+"1234").when(mockMessage).text();

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
