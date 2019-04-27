package org.synchronizer.spotify.config.properties;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.net.URI;

@Data
public class SpotifyEndpointsConfiguration {
    @NotNull
    private URI authorization;
    @NotNull
    private URI tokens;
    @NotNull
    private URI redirect;
    @NotNull
    private URI userTracks;
}
