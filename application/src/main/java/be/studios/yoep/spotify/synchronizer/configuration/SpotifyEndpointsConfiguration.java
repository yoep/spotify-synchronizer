package be.studios.yoep.spotify.synchronizer.configuration;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class SpotifyEndpointsConfiguration {
    @NotEmpty
    private String authorization;
    @NotEmpty
    private String tokens;
    @NotEmpty
    private String redirect;
}
