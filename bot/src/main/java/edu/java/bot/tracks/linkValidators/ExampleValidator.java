package edu.java.bot.tracks.linkValidators;

import java.util.regex.Pattern;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExampleValidator implements LinkValidator {

    @Getter
    private final String serviceName = "example";

    @Override
    public boolean validate(String link) {
        return Pattern.matches("(https?://)?(www.)?example.com/post/\\d+", link);
    }
}
