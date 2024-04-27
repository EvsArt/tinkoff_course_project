package edu.java.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record StackOverflowQuestionResponse(
    @JsonProperty("question_id") @NotNull Long questionId,
    @NotNull String title,
    @JsonProperty("last_activity_date") @NotNull OffsetDateTime lastActivityDate,
    @JsonProperty("answer_count") @NotNull int answerCount
) {
}
