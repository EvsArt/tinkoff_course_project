package edu.java.dto.bot;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonSerialize
public class ApiErrorResponse {
    private String description;
    private String code;
    private String exceptionName;
    private String exceptionMessage;
    private List<String> stacktrace;
}
