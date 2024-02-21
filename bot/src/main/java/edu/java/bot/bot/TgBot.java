package edu.java.bot.bot;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.UpdatesListener;

public interface TgBot extends AutoCloseable, UpdatesListener, ExceptionHandler {

    void start();

}
