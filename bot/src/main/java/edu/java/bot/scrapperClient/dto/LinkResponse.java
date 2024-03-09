package edu.java.bot.scrapperClient.dto;

import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class LinkResponse {
    private Long id;
    private URI url;
}
