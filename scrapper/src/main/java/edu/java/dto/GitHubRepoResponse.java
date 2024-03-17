package edu.java.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record GitHubRepoResponse(
    @NotNull Long id,
    @JsonProperty("full_name") @NotNull String fullName,
    @JsonProperty("updated_at") @NotNull OffsetDateTime updatedAt,
    @JsonProperty("pushed_at") @NotNull OffsetDateTime pushedAt
) {
}
