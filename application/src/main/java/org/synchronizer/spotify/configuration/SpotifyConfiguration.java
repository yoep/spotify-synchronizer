package org.synchronizer.spotify.configuration;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
@Configuration
@ConfigurationProperties("spotify")
public class SpotifyConfiguration {
    @NotEmpty
    private String clientId;
    @NotEmpty
    private String clientSecret;
    @NotNull
    @NestedConfigurationProperty
    private SpotifyEndpointsConfiguration endpoints;
}
