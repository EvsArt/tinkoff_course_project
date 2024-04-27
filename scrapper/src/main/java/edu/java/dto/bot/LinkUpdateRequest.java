package edu.java.dto.bot;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LinkUpdateRequest {

    List<Long> tgChatIds;
    private String url;
    private String description;

}
