package org.synchronizer.spotify.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("synchronizer")
public class SynchronizerConfiguration {
    private boolean cacheEnabled;
}
