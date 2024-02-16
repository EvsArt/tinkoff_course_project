package edu.java.bot.tracks;

import com.pengrad.telegrambot.model.User;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class TemporaryTracksRepository {

    private final Map<Long, Set<Track>> usersIdToTheirTracks = new HashMap<>();

    public void register(User user) {
        usersIdToTheirTracks.put(user.id(), new HashSet<>());
    }

    public boolean isRegister(User user) {
        return usersIdToTheirTracks.containsKey(user.id());
    }

    public Set<Track> getTracksByUser(User user) {
        return usersIdToTheirTracks.get(user.id());
    }

    public void addTrack(User user, Track track) {
        usersIdToTheirTracks.get(user.id()).add(track);
    }

    public void removeTrack(User user, Track track) {
        usersIdToTheirTracks.get(user.id()).remove(track);
    }

    public Optional<Track> getTrackByName(User user, String name) {
        return getTracksByUser(user).stream()
            .filter(track -> track.name().equals(name))
            .findFirst();
    }

    private final Map<Long, Integer> usersIdToLastTrackNameNum = new HashMap<>();

    /**
     * Creates new name for track in format track{N}
     *
     * @param user is user who creates a new track
     * @return new track name
     */
    public String getNewTrackNameFor(User user) {
        usersIdToLastTrackNameNum.putIfAbsent(user.id(), 0);
        // getting new trackName num and put it to the map
        int n = usersIdToLastTrackNameNum.get(user.id()) + 1;
        usersIdToLastTrackNameNum.put(user.id(), n);
        return String.format("Track%d", n);
    }

}
