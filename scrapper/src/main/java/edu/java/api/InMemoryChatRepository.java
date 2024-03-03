package edu.java.api;

import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class InMemoryChatRepository {

    private final Set<Long> chatIds = new HashSet<>();

    public boolean registry(Long id) {
        if (chatIds.contains(id)) {
            return false;
        }
        chatIds.add(id);
        return true;
    }

    public boolean delete(Long id) {
        return chatIds.remove(id);
    }

    public boolean isExists(Long id) {
        return chatIds.contains(id);
    }

}
