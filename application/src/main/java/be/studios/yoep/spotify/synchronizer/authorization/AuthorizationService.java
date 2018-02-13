package be.studios.yoep.spotify.synchronizer.authorization;

import be.studios.yoep.spotify.synchronizer.loaders.ViewLoader;
import be.studios.yoep.spotify.synchronizer.views.LoginView;
import be.studios.yoep.spotify.synchronizer.views.ViewProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Log4j2
@EqualsAndHashCode
@ToString
@Getter
@Service
public class AuthorizationService {
    private final ViewLoader viewLoader;
    private final LoginView loginView;

    private SpotifyToken token;

    //region Constructors

    /**
     * Initialize a new instance of {@link AuthorizationService}.
     *
     * @param viewLoader Set the view loader for loading the login view.
     * @param loginView  Set the login view component.
     */
    public AuthorizationService(ViewLoader viewLoader, LoginView loginView) {
        this.viewLoader = viewLoader;
        this.loginView = loginView;
    }

    //endregion

    //region Methods

    /**
     * Start the authorization process based on the given user redirect.
     *
     * @param userRedirectRequired Set the user redirect for the authorization process.
     */
    public void startAuthorization(UserRedirectRequiredException userRedirectRequired) {
        log.debug("User has not been authorized yet, starting authorization process");
        loginView.setUrl(getRedirectUrl(userRedirectRequired));
        loginView.setSuccessCallback(this::authorize);
        openLoginDialog();
    }

    /**
     * Gets the access token when it is available.
     * This method will retry based in the {@code spotifyRetry} configuration.
     *
     * @return Returns the spotify token when available.
     * @throws AccessTokenNotAvailable Is thrown when the retry policy is exhausted.
     */
    @Retryable(value = AccessTokenNotAvailable.class, interceptor = "spotifyRetry")
    public SpotifyToken getAccessTokenWhenAvailable() throws AccessTokenNotAvailable {
        return ofNullable(token).orElseThrow(AccessTokenNotAvailable::new);
    }

    //endregion

    //region Functions

    private void authorize(String url) {
        Map<String, String> params = getParameters(url);

        if (params.containsKey("code")) {
            SpotifyToken token = new SpotifyToken();
            token.setAuthorizationCode(params.get("code"));
            this.token = token;
        } else {
            log.error(params.getOrDefault("error", "Unable to obtain access code"));
        }
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

        builder.queryParam("show_dialog", "true");
        return builder.build().encode().toUriString();
    }

    private Map<String, String> getParameters(String url) {
        Map<String, String> params = new HashMap<>();
        String[] parameters = url.substring(url.indexOf("?") + 1).split("&");

        for (String param : parameters) {
            String[] paramPair = param.split("=");
            params.put(paramPair[0], paramPair[1]);
        }

        return params;
    }

    private void openLoginDialog() {
        viewLoader.showWindow("login.fxml", ViewProperties.builder()
                .maximizeDisabled(true)
                .icon("logo.png")
                .dialog(true)
                .build());
    }

    //endregion
}
