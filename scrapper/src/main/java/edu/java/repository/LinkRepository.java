package edu.java.repository;

import edu.java.model.Link;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepository {

    Optional<Link> insertLink(Link link);

    Optional<Link> updateLink(Long id, Link link);

    Optional<Link> removeLinkById(Long id);

    Optional<Link> findLinkById(Long id);

    List<Link> findAlLinks();

}
