package org.synchronizer.spotify.authorization;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.synchronizer.spotify.settings.SettingsService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
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
    public void testObtainAccessToken_whenAccessTokenSettingsIsNull_shouldObtainTokenFromSuper() throws URISyntaxException {
        OAuth2ProtectedResourceDetails details = createAuthorizationDetails();
        AccessTokenRequest parameters = createAccessTokenRequest();
        when(settingsService.getUserSettings()).thenReturn(Optional.empty());
        when(authorizationService.getAccessTokenWhenAvailable()).thenReturn(new SpotifyToken());

        spotifyAccessTokenProvider.obtainAccessToken(details, parameters);

        verify(authorizationService).getAccessTokenWhenAvailable();
    }

    private AuthorizationCodeResourceDetails createAuthorizationDetails() throws URISyntaxException {
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

    private void mockRestTemplateAuthorization(String uri) throws URISyntaxException {
        RestOperations restTemplate = spotifyAccessTokenProvider.getRestTemplate();
        HttpHeaders headers = createResponseHeaders(uri);
        ResponseEntity<Void> responseEntity = new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);

        when(restTemplate.execute(eq(uri), eq(HttpMethod.POST), isA(RequestCallback.class), isA(ResponseExtractor.class), isA(Map.class)))
                .thenReturn(responseEntity);
    }

    private HttpHeaders createResponseHeaders(String uri) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(uri));
        return headers;
    }

    // extended class of SpotifyAccessTokenProvider to override the rest template
    public static class SpotifyAccessTokenProviderMockedRestTemplate extends SpotifyAccessTokenProvider {
        private RestTemplate restTemplate = mock(RestTemplate.class);

        public SpotifyAccessTokenProviderMockedRestTemplate(AuthorizationService authorizationService, SettingsService settingsService) {
            super(authorizationService, settingsService);

            setMessageConverters(Collections.singletonList(mock(HttpMessageConverter.class)));
        }

        @Override
        public RestOperations getRestTemplate() {
            when(restTemplate.getMessageConverters()).thenReturn(Collections.singletonList(mock(HttpMessageConverter.class)));

            return restTemplate;
        }
    }
}