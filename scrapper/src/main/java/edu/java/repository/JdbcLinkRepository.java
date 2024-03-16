package edu.java.repository;

import edu.java.model.Link;
import java.util.List;
import java.util.Optional;

public class JdbcLinkRepository implements LinkRepository {

    @Override
    public Optional<Link> insertLink(Link link) {
        return Optional.empty();
    }

    @Override
    public Optional<Link> updateLink(Long id, Link link) {
        return Optional.empty();
    }

    @Override
    public Optional<Link> removeLinkById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Link> findLinkById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Link> findAlLinks() {
        return null;
    }
}
