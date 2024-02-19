package edu.java.bot.messageProcessor;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.constants.Constants;
import edu.java.bot.constants.StringService;
import joptsimple.internal.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class MessageParserTest {

    MessageParser parser = new MessageParser();

    @Mock Message message;

    @Test
    void parseCommandWithoutArgs() {
        String trigger = Constants.COMMAND_TRIGGER;
        String command = "help";
        Mockito.when(message.text()).thenReturn(trigger + command);
        MessageParser.ParsedMessage expResult = new MessageParser.ParsedMessage(true, command, List.of());

        MessageParser.ParsedMessage realResult = parser.parse(message);

        assertThat(realResult).isEqualTo(expResult);
    }

    @Test
    void parseCommandWithArgs() {
        String trigger = Constants.COMMAND_TRIGGER;
        String command = "help";
        List<String> args = List.of("oneArg", "twoArg", "threeArg");
        Mockito.when(message.text()).thenReturn(trigger + command + " " + Strings.join(args, " "));

        MessageParser.ParsedMessage expResult = new MessageParser.ParsedMessage(true, command, args);

        MessageParser.ParsedMessage realResult = parser.parse(message);

        assertThat(realResult).isEqualTo(expResult);
    }

    @Test
    void parseNotCommand() {
        String text = "Hello!";
        Mockito.when(message.text()).thenReturn(text);
        MessageParser.ParsedMessage expResult =
            new MessageParser.ParsedMessage(false, StringService.COMMAND_NOT_A_COMMAND_NAME, List.of(text));

        MessageParser.ParsedMessage realResult = parser.parse(message);

        assertThat(realResult).isEqualTo(expResult);
    }
}
