package edu.java.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record StackOverflowQuestionResponse(
    @JsonProperty("question_id") Long questionId,
    String title,
    @JsonProperty("last_activity_date") OffsetDateTime lastActivityDate
) {
}
