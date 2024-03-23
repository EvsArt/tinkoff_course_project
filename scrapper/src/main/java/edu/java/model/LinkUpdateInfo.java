package edu.java.model;

import edu.java.model.entity.Link;
import edu.java.model.entity.TgChat;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LinkUpdateInfo {
    boolean isUpdated;
    String message;
    String url;
    List<Long> tgChatsIds;

    public static LinkUpdateInfo updateInfoWithUpdate(String message, Link link) {
        return new LinkUpdateInfo(
            true,
            message,
            link.getUrl().toString(),
            link.getTgChats().stream().map(TgChat::getChatId).toList()
        );
    }

    public static LinkUpdateInfo updateInfoWithoutUpdate() {
        return new LinkUpdateInfo(false, "", "", new ArrayList<>());
    }

}
