package edu.java.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

@Getter
@ToString
@Validated
public class RemoveLinkRequest {
    @NotNull String link;
}
