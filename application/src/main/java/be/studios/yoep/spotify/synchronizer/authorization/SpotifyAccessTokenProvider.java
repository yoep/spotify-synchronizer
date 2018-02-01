package be.studios.yoep.spotify.synchronizer.authorization;

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

import static java.util.Optional.ofNullable;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class SpotifyAccessTokenProvider extends AuthorizationCodeAccessTokenProvider implements AccessTokenProvider {
    private final AuthorizationService authorizationService;

    @Override
    public OAuth2AccessToken obtainAccessToken(OAuth2ProtectedResourceDetails details, AccessTokenRequest parameters)
            throws UserRedirectRequiredException, UserApprovalRequiredException, AccessDeniedException {
        try {
            return super.obtainAccessToken(details, parameters);
        } catch (UserRedirectRequiredException ex) {
            authorizationService.startAuthorization(ex);
            SpotifyToken spotifyToken = ofNullable(authorizationService.getAccessTokenWhenAvailable())
                    .orElseThrow(AccessTokenNotAvailable::new);
            return retrieveAccessToken(details, spotifyToken.getAuthorizationCode(), ex.getStateToPreserve().toString());
        }
    }

    private OAuth2AccessToken retrieveAccessToken(OAuth2ProtectedResourceDetails resource, String authorizationCode, String redirectUri) {
        final AccessTokenRequest request = new DefaultAccessTokenRequest();

        request.setAuthorizationCode(authorizationCode);
        request.setPreservedState(redirectUri);

        return retrieveToken(request, resource, getParametersForTokenRequest(authorizationCode, redirectUri), new HttpHeaders());
    }

    private MultiValueMap<String, String> getParametersForTokenRequest(String authorizationCode,
                                                                       String redirectUri) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.set("grant_type", "authorization_code");
        form.set("code", authorizationCode);
        form.set("redirect_uri", redirectUri);
        return form;

    }
}
