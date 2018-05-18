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
    @NotNull
    @Valid
    @Builder.Default
    private Authentication authentication = Authentication.builder().build();
    @NotNull
    @Valid
    @Builder.Default
    private Logging logging = Logging.builder().build();
}
