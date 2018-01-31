package be.studios.yoep.spotify.synchronizer.authorization;

import be.studios.yoep.spotify.synchronizer.loaders.ViewLoader;
import be.studios.yoep.spotify.synchronizer.views.LoginView;
import be.studios.yoep.spotify.synchronizer.views.ViewProperties;
import lombok.Getter;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Getter
@Service
public class AuthorizationService {
    private final ViewLoader viewLoader;
    private final LoginView loginView;

    private SpotifyToken token;
    private boolean authorizing;

    public AuthorizationService(ViewLoader viewLoader, LoginView loginView) {
        this.viewLoader = viewLoader;
        this.loginView = loginView;
    }

    public void startAuthorization(UserRedirectRequiredException userRedirectRequired) {
        this.authorizing = true;
        loginView.setUrl(getRedirectUrl(userRedirectRequired));
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

    private String getRedirectUrl(UserRedirectRequiredException e) {
        String redirectUri = e.getRedirectUri();
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(redirectUri);
        Map<String, String> requestParams = e.getRequestParams();
        for (Map.Entry<String, String> param : requestParams.entrySet()) {
            builder.queryParam(param.getKey(), param.getValue());
        }

        if (e.getStateKey() != null) {
            builder.queryParam("state", e.getStateKey());
        }

        return builder.build().encode().toUriString();
    }

    private void openLoginDialog() {
        viewLoader.showWindow("login.fxml", ViewProperties.builder()
                .maximizeDisabled(true)
                .build());
    }
}
