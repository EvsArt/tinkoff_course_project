package edu.java.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

@Getter
@ToString
@Validated
public class AddLinkRequest {
    @NotBlank String link;
}
