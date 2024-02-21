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
import edu.java.bot.tracks.validator.AllLinksValidatorManager;
import edu.java.bot.tracks.validator.ExampleValidator;
import edu.java.bot.tracks.validator.LinkValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.stream.Stream;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class TrackCommandTest {

    private final String command = Constants.COMMAND_TRIGGER + StringService.COMMAND_TRACK_NAME;

    @InjectMocks TrackCommand trackCommand;
    @Spy MessageParser messageParser;
    @Spy LinkValidator exampleValidator = new ExampleValidator();
    @Spy AllLinksValidatorManager linkValidatorManager =
        new AllLinksValidatorManager(Stream.of(exampleValidator).toList());

    @Mock TemporaryTracksRepository tracksRepository;

    @Test
    void handleWithoutArgumentsItShouldPrintHelpMessage() {
        Update update = getTestUpdateMessageWithText(command);

        String expResult = StringService.commandNeedHelp(trackCommand);

        SendMessage realResponse = trackCommand.handle(update);
        String realResult = (String) realResponse.getParameters().get(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE);

        assertThat(realResult).isEqualTo(expResult);
    }

    @Test
    void handleWithOnlyOneArgumentItShouldCreateName() {
        String legalLink = "example.com/post/1";

        Update update = getTestUpdateMessageWithText(String.format("%s %s", command, legalLink));
        User user = Mockito.mock(User.class);
        Mockito.when(update.message().from()).thenReturn(user);

        String expResult = StringService.startTracking(new Track(legalLink, ""));

        SendMessage realResponse = trackCommand.handle(update);
        String realResult = (String) realResponse.getParameters().get(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE);

        Mockito.verify(tracksRepository, Mockito.times(1)).getNewTrackNameFor(update.message().from());
        assertThat(realResult).isEqualTo(expResult);
    }

    @Test
    void handleShouldReturnSuccessAndWriteTrack() {
        String legalLink = "example.com/post/1";
        String trackName = "MyTrack";
        Update update = getTestUpdateMessageWithText(String.format("%s %s %s", command, legalLink, trackName));

        Track expSavedTrack = new Track(legalLink, trackName);
        String expResult = StringService.startTracking(expSavedTrack);

        SendMessage realResponse = trackCommand.handle(update);
        String realResult = (String) realResponse.getParameters().get(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE);

        Mockito.verify(tracksRepository, Mockito.times(1)).addTrack(update.message().from(), expSavedTrack);
        assertThat(realResult).isEqualTo(expResult);
    }

    @Test
    void invalidLinkTest() {
        String illegalLink = "exmple.com/post/1";
        String trackName = "MyTrack";
        Update update = getTestUpdateMessageWithText(String.format("%s %s %s", command, illegalLink, trackName));

        String expResult = StringService.invalidTrackingLink(linkValidatorManager.getAvailableServices());

        SendMessage realResponse = trackCommand.handle(update);
        String realResult = (String) realResponse.getParameters().get(Constants.TEXT_PARAMETER_IN_SEND_MESSAGE);

        Mockito.verify(tracksRepository, Mockito.never()).addTrack(Mockito.any(), Mockito.any());
        assertThat(realResult).isEqualTo(expResult);
    }

    @Test
    void isTrigger() {
        Message mockMessage = Mockito.mock(Message.class);
        Mockito.doReturn(command).when(mockMessage).text();

        assertThat(trackCommand.isTrigger(mockMessage)).isTrue();
    }

    @Test
    void isNotTrigger() {
        Message mockMessage = Mockito.mock(Message.class);
        Mockito.doReturn(command+"1234").when(mockMessage).text();

        assertThat(trackCommand.isTrigger(mockMessage)).isFalse();
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
