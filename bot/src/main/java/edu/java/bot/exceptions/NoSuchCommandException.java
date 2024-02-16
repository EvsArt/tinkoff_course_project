package edu.java.bot.exceptions;

import edu.java.bot.constants.StringService;

public class NoSuchCommandException extends RuntimeException {

    public NoSuchCommandException(String msg) {
        super(msg);
    }

    public NoSuchCommandException() {
        super(StringService.COMMAND_NOT_EXISTS);
    }

}
