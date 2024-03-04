package edu.java.bot.scrapperClient.dto;

import lombok.Getter;
import lombok.ToString;
import java.util.List;

@Getter
@ToString
public class ListLinksResponse {

    private final List<LinkResponse> links;
    private final Integer size;

    public ListLinksResponse(List<LinkResponse> links) {
        this.links = links;
        this.size = links.size();
    }
}
