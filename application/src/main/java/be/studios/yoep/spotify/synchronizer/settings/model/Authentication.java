package be.studios.yoep.spotify.synchronizer.settings.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@Builder
@AllArgsConstructor
public class Authentication {
    @NotNull
    private OAuth2AccessTokenWrapper accessToken;
}
