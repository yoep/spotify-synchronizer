package be.studios.yoep.spotify.synchronizer.authorization;

import be.studios.yoep.spotify.synchronizer.loaders.ViewLoader;
import be.studios.yoep.spotify.synchronizer.views.ViewProperties;
import lombok.Getter;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

import static java.util.Optional.ofNullable;

@Getter
@Service
public class AuthorizationService {
    private final ViewLoader viewLoader;

    private SpotifyToken token;
    private boolean authorizing;

    public AuthorizationService(ViewLoader viewLoader) {
        this.viewLoader = viewLoader;
    }

    public void startAuthorization() {
        this.authorizing = true;
        openLoginDialog();
    }

    public void authorize(HttpServletResponse response) {
        token = new SpotifyToken();
        this.authorizing = false;
    }

    @Retryable(value = AccessTokenNotAvailable.class, interceptor = "spotifyRetry")
    public SpotifyToken getAccessTokenWhenAvailable() throws AccessTokenNotAvailable {
        return ofNullable(token).orElseThrow(AccessTokenNotAvailable::new);
    }

    private void openLoginDialog() {
        viewLoader.showWindow("login.fxml", ViewProperties.builder()
                .maximizeDisabled(true)
                .build());
    }
}
