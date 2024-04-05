package edu.java.domain.jpaRepository;

import edu.java.model.entity.Link;
import edu.java.model.entity.TgChat;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public interface JpaLinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findById(long id);

    Optional<Link> findByUrl(URI url);

    List<Link> findByLastCheckTimeIsBefore(OffsetDateTime dateTime);

    List<Link> findByTgChatsContains(TgChat chat);

    List<Link> findAll();
}
