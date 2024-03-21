package edu.java.bot.links.validator;

import edu.java.bot.links.SupportedApi;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class StackOverFlowQuestionsValidator implements LinkValidator {

    @Getter
    private final String serviceName = "StackOverFlow questions";

    @Override
    public boolean validate(String link) {
        return SupportedApi.STACKOVERFLOW_QUESTION.getLinkPattern().matcher(link).matches();
    }

}
