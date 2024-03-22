package edu.java.service;

import edu.java.model.entity.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkService {

    Link addLink(long tgChatId, URI url, String name);

    Link removeLink(long tgChatId, URI url);

    List<Link> findAllByTgChatId(long tgChatId);

    List<Link> findAll();

    Link findByUrl(URI url);

    Link findById(Long id);

    List<Link> findAllWhereLastCheckTimeBefore(OffsetDateTime dateTime);

    Link setLastCheckTime(Long linkId, OffsetDateTime dateTime);

    Link setLastUpdateTime(Long linkId, OffsetDateTime dateTime);
}
