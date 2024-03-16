package edu.java.model;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class Link {

    Long id;
    @NotNull String url;
    @NotNull String name;

    @NotNull OffsetDateTime createdAt;
    @NotNull OffsetDateTime lastUpdateTime;
    @NotNull OffsetDateTime lastCheckTime;

}
