package edu.java.dto;

import java.util.List;
import lombok.Data;

@Data
public class GitHubRepoEventsResponse {
    private List<GitHubRepoEventResponse> events;
}
