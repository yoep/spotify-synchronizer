package org.synchronizer.spotify.domain;

public interface TrackInfo {
    /**
     * Get the title of the track.
     *
     * @return Returns the title of the track.
     */
    String getTitle();

    /**
     * Get the artist of the track.
     *
     * @return Returns the title of the artist.
     */
    String getArtist();

    /**
     * Get the uri to play the track.
     * This can be from a {@link java.io.File} of {@link java.net.URI}.
     *
     * @return Returns the uri to play the track.
     */
    String getUri();

    /**
     * Get the number of the track in the album.
     *
     * @return Returns the track number of available, else null.
     */
    Integer getTrackNumber();
}
