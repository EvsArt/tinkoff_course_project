package edu.java.botClient.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LinkUpdateRequest {

    List<Long> tgChatIds;
    private String url;
    private String description;

}
