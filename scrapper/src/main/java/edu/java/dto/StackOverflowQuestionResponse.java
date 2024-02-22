package edu.java.dto;

import java.time.OffsetDateTime;

public record StackOverflowQuestionResponse(
    Long question_id,
    String title,
    OffsetDateTime last_activity_date
){
}
