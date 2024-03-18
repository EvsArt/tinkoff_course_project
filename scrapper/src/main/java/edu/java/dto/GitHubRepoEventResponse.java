package edu.java.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GitHubRepoEventResponse {
    @NotNull long id;
    @NotNull String type;
}
