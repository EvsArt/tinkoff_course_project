package edu.java.bot.links.validator;

public interface LinkValidator {

    String getServiceName();

    boolean validate(String link);
}
