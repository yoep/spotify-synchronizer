package be.studios.yoep.spotify.synchronizer.settings.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class UserSettings {
    @Valid
    @Builder.Default
    private Authentication authentication = Authentication.builder().build();
    @Valid
    @Builder.Default
    private Logging logging = Logging.builder().build();
    @Valid
    @Builder.Default
    private Synchronize synchronize = Synchronize.builder().build();
}
