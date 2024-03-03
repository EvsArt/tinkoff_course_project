package edu.java.api.dto;

import lombok.Getter;
import lombok.ToString;
import java.util.List;

@Getter
@ToString
public class ListLinksResponse {

    private List<LinkResponse> links;
    private Integer size;

    public ListLinksResponse(List<LinkResponse> links) {
        this.links = links;
        this.size = links.size();
    }

}
