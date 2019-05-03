package org.synchronizer.spotify.spotify.api.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Album {
    /**
     * The type of the album: one of “album”, “single”, or “compilation”.
     */
    private AlbumType albumType;
    /**
     * he artists of the album. Each artist object includes a link in href to more detailed information about the artist.
     */
    private List<Artist> artists;
    /**
     * The markets in which the album is available: ISO 3166-1 alpha-2 country codes. Note that an album is considered available in a market when at least 1
     * of its tracks is available in that market.
     */
    private List<String> availableMarkets;
    /**
     * Known external URLs for this album.
     */
    private Map<String, String> externalUrls;
    /**
     * A list of the genres used to classify the album.
     */
    private List<String> genres;
    /**
     * A link to the Web API endpoint providing full details of the album.
     */
    private String href;
    /**
     * The [/documentation/web-api/#spotify-uris-and-ids) for the album.
     */
    private String id;
    /**
     * The cover art for the album in various sizes, widest first.
     */
    private List<Image> images;
    /**
     * The name of the album. In case of an album takedown, the value may be an empty string.
     */
    private String name;
    /**
     * The tracks of the album.
     */
    private AlbumTracks tracks;
    /**
     * The number of tracks in the album.
     */
    private Integer total_tracks;
    /**
     * The object type: “album”
     */
    private String type;
    /**
     * The Spotify URI for the album.
     */
    private String uri;
}
