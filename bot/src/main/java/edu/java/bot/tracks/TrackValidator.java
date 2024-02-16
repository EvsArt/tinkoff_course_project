package edu.java.bot.tracks;

import java.util.List;

public interface TrackValidator {

    boolean validateLink(String link);

    List<String> getAvailableServices();

}
