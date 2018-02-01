package be.studios.yoep.spotify.synchronizer.authorization;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
public class SpotifyToken extends AbstractAuthenticationToken {
    private String authorizationCode;
    private OAuth2AccessToken accessToken;

    public SpotifyToken() {
        super(new ArrayList<>());
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
