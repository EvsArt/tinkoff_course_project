package edu.java.bot.scrapperClient.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AddLinkRequest {
    @NotNull String link;
}
