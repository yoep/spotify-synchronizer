package be.studios.yoep.spotify.synchronizer.settings.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {
    @NotNull
    @Valid
    @Builder.Default
    private Authentication authentication = new Authentication();
    @NotNull
    @Valid
    @Builder.Default
    private Logging logging = new Logging();
}
