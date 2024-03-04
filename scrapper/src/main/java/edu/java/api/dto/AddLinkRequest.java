package edu.java.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

@Getter
@ToString
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class AddLinkRequest {
    @NotBlank String link;
}
