package org.synchronizer.spotify.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
@Configuration
@ConfigurationProperties("synchronizer")
public class SynchronizerConfiguration {
    @NotNull
    private CacheMode cacheMode;
}
