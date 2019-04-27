package org.synchronizer.spotify.spotify.api.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Track {
    /**
     * The album on which the track appears. The album object includes a link in href to full information about the album.
     */
    private Album album;
    /**
     * The artists who performed the track. Each artist object includes a link in href to more detailed information about the artist.
     */
    private List<Artist> artists;
    /**
     * A list of the countries in which the track can be played, identified by their ISO 3166-1 alpha-2 code.
     */
    private List<String> availableMarkets;
    /**
     * The disc number (usually 1 unless the album consists of more than one disc).
     */
    private Integer discNumber;
    /**
     * The track length in milliseconds.
     */
    private Integer durationMs;
    /**
     * Whether or not the track has explicit lyrics (true = yes it does; false = no it does not OR unknown).
     */
    private Boolean explicit;
    /**
     * Known external IDs for the track.
     */
    private Map<String, String> externalIds;
    /**
     * Known external URLs for this track.
     */
    private Map<String, String> externalUrls;
    /**
     * A link to the Web API endpoint providing full details of the track.
     */
    private String href;
    /**
     * The Spotify ID for the track.
     */
    private String id;
    /**
     * Part of the response when Track Relinking is applied. If true, the track is playable in the given market. Otherwise false.
     */
    private Boolean isPlayable;
    /**
     * The name of the track.
     */
    private String name;
    /**
     * The popularity of the track. The value will be between 0 and 100, with 100 being the most popular.
     */
    private Integer popularity;
    /**
     * A link to a 30 second preview (MP3 format) of the track. null if not available.
     */
    private String previewUrl;
    /**
     * The number of the track. If an album has several discs, the track number is the number on the specified disc.
     */
    private Integer trackNumber;
    private String type;
    /**
     * The Spotify URI for the track.
     */
    private String uri;
}
