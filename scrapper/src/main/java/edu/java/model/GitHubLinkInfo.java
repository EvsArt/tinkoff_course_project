package edu.java.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class GitHubLinkInfo {

    private long id;
    @NonNull private Link link;
    @NonNull private Long lastEventId;

}
