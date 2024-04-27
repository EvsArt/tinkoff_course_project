package edu.java.bot.dto.scrapperClient;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ListLinksResponse {

    private final List<LinkResponse> links;
    private final Integer size;

    public ListLinksResponse(List<LinkResponse> links) {
        this.links = links;
        this.size = links.size();
    }
}
