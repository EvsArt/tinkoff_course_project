package edu.java.domain;

import edu.java.model.entity.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepository {

    Optional<Link> insertLink(Link link);

    Optional<Link> updateLink(Long id, Link link);

    Optional<Link> removeLinkById(Long id);

    Optional<Link> findLinkById(Long id);

    Optional<Link> findLinkByURL(URI url);

    List<Link> findAllLinks();

    List<Link> findLinksByTgChatId(Long id);

    List<Link> removeLinksByTgChatId(Long id);

    Optional<Link> removeLinkByTgChatIdAndUri(Long id, URI uri);

    List<Link> findAllWhereLastCheckTimeBefore(OffsetDateTime dateTime);

}
