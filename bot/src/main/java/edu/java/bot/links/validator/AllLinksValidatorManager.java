package edu.java.bot.links.validator;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllLinksValidatorManager implements LinkValidatorManager {

    private final List<LinkValidator> linkValidators;

    @Autowired
    public AllLinksValidatorManager(List<LinkValidator> linkValidators) {
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
