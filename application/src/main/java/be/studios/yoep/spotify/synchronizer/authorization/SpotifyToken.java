package be.studios.yoep.spotify.synchronizer.authorization;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.ArrayList;

public class SpotifyToken extends AbstractAuthenticationToken {

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
