package edu.java.dto;

import edu.java.model.Link;
import lombok.Data;
import java.util.List;

@Data
public class GitHubRepoEventsResponse {
    private List<GitHubRepoEventResponse> events;
}
