package be.studios.yoep.spotify.synchronizer.authorization;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserApprovalRequiredException;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

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
            return ofNullable(authorizationService.getAccessTokenWhenAvailable())
                    .orElseThrow(AccessTokenNotAvailable::new)
                    .getAccessToken();
        }
    }
}
