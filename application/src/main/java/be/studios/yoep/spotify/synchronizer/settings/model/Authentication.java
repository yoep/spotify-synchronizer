package be.studios.yoep.spotify.synchronizer.settings.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Observable;

@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authentication extends Observable implements Serializable {
    @NotNull
    private OAuth2AccessTokenWrapper accessToken;

    public void setAccessToken(OAuth2AccessTokenWrapper accessToken) {
        this.accessToken = accessToken;
        this.setChanged();
        this.notifyObservers();
    }
}
