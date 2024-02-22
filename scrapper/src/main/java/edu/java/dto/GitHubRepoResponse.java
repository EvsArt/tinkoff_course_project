package edu.java.dto;

import org.springframework.beans.factory.annotation.Value;
import java.time.OffsetDateTime;

public record GitHubRepoResponse(
    Long id,
    String full_name,
    OffsetDateTime updated_at,
    OffsetDateTime pushed_at
) {
}
