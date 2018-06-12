package be.studios.yoep.spotify.synchronizer.authorization;

import be.studios.yoep.spotify.synchronizer.settings.UserSettingsService;
import be.studios.yoep.spotify.synchronizer.settings.model.Authentication;
import be.studios.yoep.spotify.synchronizer.settings.model.OAuth2AccessTokenWrapper;
import be.studios.yoep.spotify.synchronizer.settings.model.UserSettings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserApprovalRequiredException;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class SpotifyAccessTokenProvider extends AuthorizationCodeAccessTokenProvider implements AccessTokenProvider {
    private final AuthorizationService authorizationService;
    private final UserSettingsService settingsService;

    @Override
    public OAuth2AccessToken obtainAccessToken(OAuth2ProtectedResourceDetails details, AccessTokenRequest parameters)
            throws UserRedirectRequiredException, UserApprovalRequiredException, AccessDeniedException {
        try {
            Optional<OAuth2AccessTokenWrapper> optionalAccessToken = getAccessTokenFromSettings();

            return optionalAccessToken
                    .map(accessTokenWrapper -> resolveAccessToken(details, accessTokenWrapper))
                    .orElseGet(() -> super.obtainAccessToken(details, parameters));
        } catch (RefreshTokenMissingException ex) {
            logger.error(ex);

            return handleRefreshTokenMissing(details, parameters);
        } catch (UserRedirectRequiredException ex) {
            return startUserRedirect(details, ex);
        }
    }

    private OAuth2AccessToken startUserRedirect(OAuth2ProtectedResourceDetails details, UserRedirectRequiredException ex) {
        authorizationService.startAuthorization(ex);
        SpotifyToken spotifyToken = ofNullable(authorizationService.getAccessTokenWhenAvailable())
                .orElseThrow(AccessTokenNotAvailable::new);
        return retrieveAccessToken(details, spotifyToken.getAuthorizationCode(), ex.getStateToPreserve().toString());
    }

    private OAuth2AccessToken resolveAccessToken(OAuth2ProtectedResourceDetails details, OAuth2AccessTokenWrapper accessTokenWrapper) {
        if (!accessTokenWrapper.isExpired()) {
            return accessTokenWrapper.getToken();
        } else if (accessTokenWrapper.getToken().getRefreshToken() != null) {
            return retrieveRefreshToken(details, accessTokenWrapper.getToken());
        } else {
            throw new RefreshTokenMissingException();
        }
    }

    private OAuth2AccessToken retrieveAccessToken(OAuth2ProtectedResourceDetails resource, String authorizationCode, String redirectUri) {
        final AccessTokenRequest request = new DefaultAccessTokenRequest();

        request.setAuthorizationCode(authorizationCode);
        request.setPreservedState(redirectUri);

        OAuth2AccessToken oAuth2AccessToken = retrieveToken(request, resource, getParametersForTokenRequest(authorizationCode, redirectUri), new HttpHeaders());
        saveAccessToken(oAuth2AccessToken);
        return oAuth2AccessToken;
    }

    private OAuth2AccessToken retrieveRefreshToken(OAuth2ProtectedResourceDetails resource, OAuth2AccessToken accessToken) {
        final AccessTokenRequest request = new DefaultAccessTokenRequest();

        OAuth2AccessToken oAuth2AccessToken = retrieveToken(request, resource, getParametersForRefreshTokenRequest(accessToken), new HttpHeaders());
        saveAccessToken(oAuth2AccessToken);
        return oAuth2AccessToken;
    }

    private OAuth2AccessToken handleRefreshTokenMissing(OAuth2ProtectedResourceDetails details, AccessTokenRequest parameters) {
        try {
            return super.obtainAccessToken(details, parameters);
        } catch (UserRedirectRequiredException userRedirect) {
            return startUserRedirect(details, userRedirect);
        }
    }

    private MultiValueMap<String, String> getParametersForTokenRequest(String authorizationCode,
                                                                       String redirectUri) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.set("grant_type", "authorization_code");
        form.set("code", authorizationCode);
        form.set("redirect_uri", redirectUri);
        return form;
    }

    private MultiValueMap<String, String> getParametersForRefreshTokenRequest(OAuth2AccessToken accessToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.set("grant_type", "refresh_token");
        form.set("refresh_token", accessToken.getRefreshToken().getValue());
        return form;
    }

    private Optional<OAuth2AccessTokenWrapper> getAccessTokenFromSettings() {
        return settingsService.getUserSettings()
                .map(UserSettings::getAuthentication)
                .map(Authentication::getAccessToken);
    }

    private void saveAccessToken(OAuth2AccessToken oAuth2AccessToken) {
        UserSettings userSettings = settingsService.getUserSettings()
                .orElse(UserSettings.builder().build());

        userSettings.setAuthentication(new Authentication(OAuth2AccessTokenWrapper.builder()
                .expireDate(LocalDateTime.ofInstant(oAuth2AccessToken.getExpiration().toInstant(), ZoneId.systemDefault()))
                .token(oAuth2AccessToken)
                .build()));
        settingsService.save(userSettings);
    }
}
