package edu.java.bot.dto.scrapperClient;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class AddLinkRequest {
    @NotNull String link;
}
