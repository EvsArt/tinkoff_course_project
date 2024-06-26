package edu.java.dto.github;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GitHubRepoEventResponse {
    @NotNull long id;
    @NotNull String type;
}
