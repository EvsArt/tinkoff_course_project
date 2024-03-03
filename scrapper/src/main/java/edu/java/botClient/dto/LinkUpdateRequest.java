package edu.java.botClient.dto;

import java.util.List;

public class LinkUpdateRequest {

    private Long id;
    private String url;
    private String description;
    List<Long> tgChatIds;

}
