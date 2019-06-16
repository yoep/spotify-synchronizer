package org.synchronizer.spotify.settings.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
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
        if (!Objects.equals(this.accessToken, accessToken))
            this.setChanged();

        this.accessToken = accessToken;
        this.notifyObservers();
    }
}
