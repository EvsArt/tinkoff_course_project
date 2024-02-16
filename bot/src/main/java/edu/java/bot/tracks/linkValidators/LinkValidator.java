package edu.java.bot.tracks.linkValidators;

public interface LinkValidator {

    String getServiceName();

    boolean validate(String link);
}
