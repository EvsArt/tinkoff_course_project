package edu.java.bot.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ToString
@Builder
public class LinkUpdateRequest {
    @NotNull
    private String url;
    private String description;
    @NotEmpty
    private List<Long> tgChatIds;
}
