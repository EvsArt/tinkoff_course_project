package edu.java.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record StackOverflowQuestionResponse(
    @JsonProperty("question_id") @NotNull Long questionId,
    @NotNull String title,
    @JsonProperty("last_activity_date") @NotNull OffsetDateTime lastActivityDate
) {
}
