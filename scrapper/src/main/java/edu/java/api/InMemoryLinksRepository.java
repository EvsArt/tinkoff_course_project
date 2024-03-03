package edu.java.api;

import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class InMemoryLinksRepository {

    private final Map<Long, Set<Link>> tgChatToLinksMap = new HashMap<>();

    public void addLink(Long chatId, Link link) {
        tgChatToLinksMap.putIfAbsent(chatId, new HashSet<>());
        tgChatToLinksMap.get(chatId).add(link);
    }

    public boolean removeLink(Long chatId, Link link) {
        tgChatToLinksMap.putIfAbsent(chatId, new HashSet<>());
        return tgChatToLinksMap.get(chatId).remove(link);
    }

    public List<Link> getLinksByChatId(Long chatId) {
        return (tgChatToLinksMap.containsKey(chatId)) ? tgChatToLinksMap.get(chatId).stream().toList() : new ArrayList<>();
    }

    public record Link(Long id, String url){}

}
