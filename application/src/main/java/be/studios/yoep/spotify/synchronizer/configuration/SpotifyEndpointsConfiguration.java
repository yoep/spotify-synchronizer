package be.studios.yoep.spotify.synchronizer.configuration;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.net.URI;

@Data
public class SpotifyEndpointsConfiguration {
    @NotEmpty
    private URI authorization;
    @NotEmpty
    private URI tokens;
    @NotEmpty
    private URI redirect;
    @NotEmpty
    private URI userTracks;
}
