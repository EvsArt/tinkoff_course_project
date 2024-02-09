package edu.java.bot.exceptions;

public class NoSuchCommandException extends RuntimeException {

    public NoSuchCommandException(String msg) {
        super(msg);
    }

    public NoSuchCommandException() {
        super("");
    }

}
