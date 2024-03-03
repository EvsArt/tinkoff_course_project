package edu.java.bot.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@ToString
public class LinkUpdateRequest {
    @NotNull
    private Long id;
    @NotNull
    private String url;
    private String description;
    @NotEmpty
    private List<Long> tgChatIds;
}
