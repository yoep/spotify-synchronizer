package org.synchronizer.spotify.spotify;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;

import java.net.URI;

public class OAuth2RestTemplateSpotify extends OAuth2RestTemplate {
    private OAuth2ClientContext context;

    public OAuth2RestTemplateSpotify(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context) {
        super(resource, context);
        this.context = context;
    }

    @Override
    protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) throws RestClientException {
        try {
            return super.doExecute(url, method, requestCallback, responseExtractor);
        } catch (HttpClientErrorException ex) {
            OAuth2AccessToken accessToken = context.getAccessToken();

            if (accessToken != null && ex.getRawStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                acquireAccessToken(context);
                return super.doExecute(url, method, requestCallback, responseExtractor);
            }

            throw ex;
        }
    }
}
