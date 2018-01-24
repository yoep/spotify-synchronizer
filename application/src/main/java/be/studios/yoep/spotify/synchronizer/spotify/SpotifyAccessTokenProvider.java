package be.studios.yoep.spotify.synchronizer.spotify;

import lombok.extern.log4j.Log4j2;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserApprovalRequiredException;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

@Log4j2
public class SpotifyAccessTokenProvider extends AuthorizationCodeAccessTokenProvider {
    @Override
    public OAuth2AccessToken obtainAccessToken(OAuth2ProtectedResourceDetails details, AccessTokenRequest request)
            throws UserRedirectRequiredException, UserApprovalRequiredException, AccessDeniedException, OAuth2AccessDeniedException {
        if (request.getAuthorizationCode() == null) {
            try {

                checkAccessTokenIsAvailable();
            } catch (Exception e) {
                log.error("Failed to open URI", e);
            }
        }

        return null;
    }

    @Retryable(AccessTokenNotAvailable.class)
    public void checkAccessTokenIsAvailable() {

    }
}
