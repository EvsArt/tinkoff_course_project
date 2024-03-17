package edu.java.repository;

import edu.java.model.Link;
import java.net.URI;
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
}
