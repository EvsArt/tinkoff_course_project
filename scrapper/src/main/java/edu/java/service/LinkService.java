package edu.java.service;

import edu.java.model.Link;
import java.net.URI;
import java.util.List;

public interface LinkService {

    Link addLink(long tgChatId, URI url, String name);

    Link removeLink(long tgChatId, URI url);

    List<Link> findAll(long tgChatId);

}
