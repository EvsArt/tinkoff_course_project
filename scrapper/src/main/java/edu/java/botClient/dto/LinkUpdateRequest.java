package edu.java.botClient.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
public class LinkUpdateRequest {

    List<Long> tgChatIds;
    private String url;
    private String description;

}
