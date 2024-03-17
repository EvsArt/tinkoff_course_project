package edu.java.model;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Link {

    Long id;
    @NonNull URI url;
    @NonNull String name;

    @NonNull OffsetDateTime createdAt;
    @NonNull OffsetDateTime lastUpdateTime;
    @NonNull OffsetDateTime lastCheckTime;

    List<TgChat> tgChats = new ArrayList<>();

    public Link(URI url, String name) {
        this.url = url;
        this.name = name;
        this.createdAt = OffsetDateTime.now();
        this.lastUpdateTime = OffsetDateTime.now();
        this.lastCheckTime = OffsetDateTime.now();
    }

}
