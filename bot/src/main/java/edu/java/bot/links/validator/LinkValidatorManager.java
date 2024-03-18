package edu.java.bot.links.validator;

import java.util.List;

public interface LinkValidatorManager {

    boolean validateLink(String link);

    List<String> getAvailableServices();

}
