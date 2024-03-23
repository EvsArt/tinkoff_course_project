package edu.java.service.jpa;

import edu.java.domain.jpaRepository.JpaTgChatRepository;
import edu.java.exceptions.ChatAlreadyRegisteredException;
import edu.java.exceptions.ChatNotExistException;
import edu.java.model.entity.TgChat;
import edu.java.service.TgChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class JpaTgChatService implements TgChatService {

    private final JpaTgChatRepository tgChatRepository;

    public JpaTgChatService(JpaTgChatRepository tgChatRepository) {
        this.tgChatRepository = tgChatRepository;
    }

    @Override
    @Transactional
    public TgChat registerChat(long tgChatId, String name) {
        log.debug("registerChat() was called with tgChatId={}, name={}", tgChatId, name);
        TgChat chat = new TgChat(tgChatId, name);
        try {
            return tgChatRepository.save(chat);
        } catch (DataIntegrityViolationException e) {
            throw new ChatAlreadyRegisteredException();
        }
    }

    @Override
    @Transactional
    public TgChat unregisterChat(long tgChatId) {
        log.debug("unregisterChat() was called with tgChatId={}", tgChatId);
        TgChat oldChat = tgChatRepository.findTgChatByChatId(tgChatId).orElseThrow(ChatNotExistException::new);
        tgChatRepository.deleteByChatId(tgChatId);
        return oldChat;
    }
}
