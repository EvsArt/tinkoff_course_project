package edu.java.bot.links.validator;

import edu.java.bot.links.SupportedApi;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
public class GitHubRepoValidator implements LinkValidator {

    @Getter
    private final String serviceName = "GitHub repository";

    @Override
    public boolean validate(String link) {
        return SupportedApi.GITHUB_REPO.getLinkPattern().matcher(link).matches();
    }
}
