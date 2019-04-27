package org.synchronizer.spotify.authorization;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestOperations;
import org.synchronizer.spotify.settings.SettingsService;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SpotifyAccessTokenProviderTest {
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private SettingsService settingsService;
    @InjectMocks
    private SpotifyAccessTokenProviderMockedRestTemplate spotifyAccessTokenProvider;

    @Test
    @Ignore //TODO: fix test
    public void testObtainAccessToken_whenAccessTokenSettingsIsNull_shouldObtainTokenFromSuper() {
        OAuth2ProtectedResourceDetails details = createAuthorizationDetails();
        AccessTokenRequest parameters = createAccessTokenRequest();
        when(settingsService.getUserSettings()).thenReturn(Optional.empty());
        when(authorizationService.getAccessTokenWhenAvailable()).thenReturn(new SpotifyToken());

        spotifyAccessTokenProvider.obtainAccessToken(details, parameters);

        verify(authorizationService).getAccessTokenWhenAvailable();
    }

    private AuthorizationCodeResourceDetails createAuthorizationDetails() {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        String userAuthorizationUri = "http://localhost:9600";
        mockRestTemplateAuthorization(userAuthorizationUri);

        details.setAccessTokenUri("http://localhost:9600/token");
        details.setUserAuthorizationUri(userAuthorizationUri);

        return details;
    }

    private AccessTokenRequest createAccessTokenRequest() {
        DefaultAccessTokenRequest tokenRequest = new DefaultAccessTokenRequest();

        tokenRequest.setStateKey("ebwpwI");
        tokenRequest.setPreservedState("");

        return tokenRequest;
    }

    private void mockRestTemplateAuthorization(String uri) {
        RestOperations restTemplate = spotifyAccessTokenProvider.getRestTemplate();
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.OK);

        when(restTemplate.execute(eq(uri), eq(HttpMethod.POST), isA(RequestCallback.class), isA(ResponseExtractor.class), isA(Map.class)))
                .thenReturn(responseEntity);
    }

    // extended class of SpotifyAccessTokenProvider to override the rest template
    public static class SpotifyAccessTokenProviderMockedRestTemplate extends SpotifyAccessTokenProvider {
        private RestOperations restTemplate = mock(RestOperations.class);

        public SpotifyAccessTokenProviderMockedRestTemplate(AuthorizationService authorizationService, SettingsService settingsService) {
            super(authorizationService, settingsService);
        }

        @Override
        public RestOperations getRestTemplate() {
            return restTemplate;
        }
    }
}