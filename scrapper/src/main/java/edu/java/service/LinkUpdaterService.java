package edu.java.service;

import edu.java.model.Link;
import edu.java.model.LinkUpdateInfo;

public interface LinkUpdaterService {
    LinkUpdateInfo checkUpdates(Link link);
}
