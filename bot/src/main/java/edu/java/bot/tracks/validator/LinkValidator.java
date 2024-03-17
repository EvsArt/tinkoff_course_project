package edu.java.bot.tracks.validator;

public interface LinkValidator {

    String getServiceName();

    boolean validate(String link);
}
