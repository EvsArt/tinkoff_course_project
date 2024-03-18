package edu.java.bot.scrapperClient.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class RemoveLinkRequest {
    @NotNull String link;
}
