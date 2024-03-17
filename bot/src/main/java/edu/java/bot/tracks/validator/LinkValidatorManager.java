package edu.java.bot.tracks.validator;

import java.util.List;

public interface LinkValidatorManager {

    boolean validateLink(String link);

    List<String> getAvailableServices();

}
