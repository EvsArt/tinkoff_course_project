package edu.java.bot.bot;

import com.pengrad.telegrambot.UpdatesListener;

public interface TgBot extends AutoCloseable, UpdatesListener {

    void start();

}
