package edu.java.service;

import edu.java.model.entity.TgChat;

public interface TgChatService {

    TgChat registerChat(long tgChatId, String name);

    TgChat unregisterChat(long tgChatId);

}
