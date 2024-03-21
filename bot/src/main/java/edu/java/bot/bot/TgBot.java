package edu.java.bot.bot;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;

public interface TgBot extends AutoCloseable, UpdatesListener, ExceptionHandler {

    void start();

    void sendMessage(SendMessage message);

}
