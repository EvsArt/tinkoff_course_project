package edu.java.model.entity;

import edu.java.service.converter.UriStringConverter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Delegate;
import org.hibernate.validator.constraints.UniqueElements;
import org.jetbrains.annotations.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class Link implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = UriStringConverter.class)
    @NonNull private URI url;
    @NonNull private String name;

    @NonNull private OffsetDateTime createdAt;
    @NonNull private OffsetDateTime lastUpdateTime;
    @NonNull private OffsetDateTime lastCheckTime;

    @Delegate
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "link_tg_chat",
               inverseJoinColumns = @JoinColumn(name = "tg_chat_id"))
    private Set<TgChat> tgChats = new HashSet<>();

    public Link(@NotNull URI url, @NotNull String name) {
        this.url = url;
        this.name = name;
        this.createdAt = OffsetDateTime.now();
        this.lastUpdateTime = OffsetDateTime.now();
        this.lastCheckTime = OffsetDateTime.now();
    }

}
