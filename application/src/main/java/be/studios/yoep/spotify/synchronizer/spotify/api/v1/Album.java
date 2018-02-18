package be.studios.yoep.spotify.synchronizer.spotify.api.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Album {
    private AlbumType albumType;
    private List<Artist> artists;
    private List<String> availableMarkets;
    private Map<String, String> externalUrls;
    private String href;
    private String id;
    private List<Image> images;
    private String name;
    private String type;
    private String uri;
}
