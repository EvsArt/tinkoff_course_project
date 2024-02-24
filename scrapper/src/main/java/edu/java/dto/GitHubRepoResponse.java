package edu.java.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record GitHubRepoResponse(
    Long id,
    @JsonProperty("full_name") String fullName,
    @JsonProperty("updated_at") OffsetDateTime updatedAt,
    @JsonProperty("pushed_at") OffsetDateTime pushedAt
) {
}
