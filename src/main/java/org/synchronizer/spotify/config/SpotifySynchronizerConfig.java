package org.synchronizer.spotify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.synchronizer.spotify.authorization.AuthorizationService;
import org.synchronizer.spotify.authorization.SpotifyAccessTokenProvider;
import org.synchronizer.spotify.config.properties.SpotifyConfiguration;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.spotify.OAuth2RestTemplateSpotify;
import org.synchronizer.spotify.ui.UIText;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

@Configuration
public class SpotifySynchronizerConfig {

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(UIText.DIRECTORY + "splash", UIText.DIRECTORY + "menu", UIText.DIRECTORY + "settings", UIText.DIRECTORY + "main", UIText.DIRECTORY + "sync");
        return messageSource;
    }

    @Bean
    public AuthorizationCodeResourceDetails spotifyAuthorization(SpotifyConfiguration configuration) {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setClientId(configuration.getClientId());
        details.setClientSecret(configuration.getClientSecret());
        details.setScope(Collections.singletonList("user-library-read"));
        details.setAccessTokenUri(configuration.getEndpoints().getTokens().toString());
        details.setUserAuthorizationUri(configuration.getEndpoints().getAuthorization().toString());
        details.setPreEstablishedRedirectUri(configuration.getEndpoints().getRedirect().toString());
        details.setUseCurrentUri(false);
        return details;
    }

    @Bean
    public OAuth2RestTemplate spotifyRestTemplate(AuthorizationCodeResourceDetails spotifyAuthorization,
                                                  AuthorizationService authorizationService,
                                                  SettingsService settingsService,
                                                  MappingJackson2HttpMessageConverter jackson2HttpMessageConverter) {
        OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplateSpotify(spotifyAuthorization, new DefaultOAuth2ClientContext());
        oAuth2RestTemplate.setAccessTokenProvider(new AccessTokenProviderChain(asList(
                new SpotifyAccessTokenProvider(authorizationService, settingsService),
                new AuthorizationCodeAccessTokenProvider(),
                new ImplicitAccessTokenProvider(),
                new ResourceOwnerPasswordAccessTokenProvider(),
                new ClientCredentialsAccessTokenProvider())));
        List<HttpMessageConverter<?>> messageConverters = oAuth2RestTemplate.getMessageConverters();
        messageConverters.add(6, jackson2HttpMessageConverter);
        return oAuth2RestTemplate;
    }
}
