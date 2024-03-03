package edu.java.api.dto;

import java.util.List;
import lombok.Getter;
import lombok.ToString;

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
