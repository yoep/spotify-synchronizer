package org.synchronizer.spotify.synchronize.model;

import org.springframework.util.Assert;
import org.synchronizer.spotify.common.AbstractObservable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractMusicTrack extends AbstractObservable implements MusicTrack, Serializable {
    @Override
    public boolean matches(MusicTrack musicTrack) {
        Assert.notNull(musicTrack, "musicTrack cannot be null");
        String title = removeSpecialChars(getTitle()).trim();
        String compareTitle = removeSpecialChars(musicTrack.getTitle()).trim();
        String artist = removeSpecialChars(getArtist()).trim();
        String compareArtist = removeSpecialChars(musicTrack.getArtist()).trim();

        return title.equalsIgnoreCase(compareTitle) &&
                artist.equalsIgnoreCase(compareArtist);
    }

    @Override
    public int compareTo(MusicTrack compareTo) {
        Assert.notNull(compareTo, "compareTo cannot be null");

        //compare against the track number if possible
        if (this.getTrackNumber() != null) {
            if (compareTo.getTrackNumber() == null)
                return 1;

            return Objects.compare(this.getTrackNumber(), compareTo.getTrackNumber(), Integer::compareTo);
        }

        //else, compare the title of the track
        return Objects.compare(this.getTitle(), compareTo.getTitle(), String::compareTo);
    }

    private String removeSpecialChars(String value) {
        return Optional.ofNullable(value)
                .map(e -> e.replaceAll("[^a-zA-Z0-9]", ""))
                .orElse("");
    }
}
