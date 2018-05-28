package be.studios.yoep.spotify.synchronizer.settings.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authentication implements Serializable {
    @NotNull
    private OAuth2AccessTokenWrapper accessToken;
}
