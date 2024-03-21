package edu.java.service;

import edu.java.model.TgChat;

public interface TgChatService {

    TgChat registerChat(long tgChatId, String name);

    TgChat unregisterChat(long tgChatId);

}
