package edu.java.botClient.dto;

import java.util.List;

public class ApiErrorResponse {
    String description;
    String code;
    String exceptionName;
    String exceptionMessage;
    List<String> stacktrace;
}
