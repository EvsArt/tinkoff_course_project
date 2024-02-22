package edu.java.dto;

import java.util.List;

public record StackOverflowQuestionListResponse(
    List<StackOverflowQuestionResponse> items
    ) {
}
