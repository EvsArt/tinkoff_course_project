package edu.java.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RemoveLinkRequest {
    @NotNull String link;
}
