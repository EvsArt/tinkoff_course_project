package edu.java.service;

import edu.java.model.LinkUpdateInfo;
import edu.java.model.entity.Link;

public interface LinkUpdaterService {
    LinkUpdateInfo checkUpdates(Link link);
}
