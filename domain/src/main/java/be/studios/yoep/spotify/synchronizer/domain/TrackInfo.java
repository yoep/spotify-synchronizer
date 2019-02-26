package be.studios.yoep.spotify.synchronizer.domain;

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
}
