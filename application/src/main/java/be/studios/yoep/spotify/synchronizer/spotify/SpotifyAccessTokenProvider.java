package be.studios.yoep.spotify.synchronizer.spotify;

import be.studios.yoep.spotify.synchronizer.authorization.AccessTokenNotAvailable;
import be.studios.yoep.spotify.synchronizer.authorization.AuthorizationService;
import be.studios.yoep.spotify.synchronizer.loaders.ViewLoader;
import be.studios.yoep.spotify.synchronizer.views.LoginView;
import be.studios.yoep.spotify.synchronizer.views.ViewProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserApprovalRequiredException;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class SpotifyAccessTokenProvider extends AuthorizationCodeAccessTokenProvider {
    private final ViewLoader viewLoader;
    private final LoginView loginView;
    private final AuthorizationService authorizationService;

    public SpotifyAccessTokenProvider(ViewLoader viewLoader, LoginView loginView, AuthorizationService authorizationService) {
        this.viewLoader = viewLoader;
        this.loginView = loginView;
        this.authorizationService = authorizationService;
    }

    @Override
    public OAuth2AccessToken obtainAccessToken(OAuth2ProtectedResourceDetails details, AccessTokenRequest request)
            throws UserRedirectRequiredException, UserApprovalRequiredException, AccessDeniedException, OAuth2AccessDeniedException {
        if (request.getAuthorizationCode() == null) {
            try {
                openLoginDialog();
                authorizationService.getAccessTokenWhenAvailable();
            } catch (AccessTokenNotAvailable ex) {
                throw ex;
            } catch (Exception ex) {
                log.error("Failed to open URI", ex);
            }
        }

        return null;
    }

    private void openLoginDialog() {
        viewLoader.showWindow("login.fxml", ViewProperties.builder()
                .maximizeDisabled(true)
                .build());
    }
}
