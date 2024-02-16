package edu.java.bot.tracks;

import edu.java.bot.tracks.linkValidators.LinkValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LinkValidatorManager implements TrackValidator {

    private final List<LinkValidator> linkValidators;

    @Autowired
    public LinkValidatorManager(List<LinkValidator> linkValidators) {
        this.linkValidators = linkValidators;
    }

    public boolean validateLink(String link) {
        return linkValidators.stream().anyMatch(it -> it.validate(link));
    }

    public List<String> getAvailableServices() {
        return linkValidators.stream()
            .map(LinkValidator::getServiceName)
            .toList();
    }

}
