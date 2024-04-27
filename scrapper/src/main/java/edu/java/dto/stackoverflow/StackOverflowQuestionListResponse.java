package edu.java.dto.stackoverflow;

import java.util.List;

public record StackOverflowQuestionListResponse(
    List<StackOverflowQuestionResponse> items
) {
}
