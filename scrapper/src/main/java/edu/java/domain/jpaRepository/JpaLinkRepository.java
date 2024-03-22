package edu.java.domain.jpaRepository;

import edu.java.model.entity.Link;
import edu.java.model.entity.TgChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaLinkRepository extends JpaRepository<Link, Long> {

    int deleteLinkByTgChatsContainsAndUrl(TgChat tgChat, URI url);

    Optional<Link> findById(long id);
    Optional<Link> findByUrl(URI url);

    List<Link> findByLastCheckTimeIsBefore(OffsetDateTime dateTime);

    List<Link> findByTgChatsContains(TgChat chat);

    boolean existsByUrl(URI url);
}
