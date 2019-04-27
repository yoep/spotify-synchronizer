package org.synchronizer.spotify.synchronize.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.synchronizer.spotify.common.AbstractObservable;

import java.util.Objects;

public abstract class AbstractMusicTrack extends AbstractObservable implements MusicTrack {
    @Override
    public boolean matches(MusicTrack musicTrack) {
        Assert.notNull(musicTrack, "musicTrack cannot be null");

        if (StringUtils.isEmpty(getTitle()) || StringUtils.isEmpty(musicTrack.getTitle())) {
            return false;
        }

        return getTitle().trim().toLowerCase().equals(musicTrack.getTitle().trim().toLowerCase()) &&
                getArtist().trim().toLowerCase().equals(musicTrack.getArtist().trim().toLowerCase());
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
}
