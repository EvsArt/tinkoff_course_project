package edu.java.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class LinkResponse {
    private Long id;
    private String url;
}
