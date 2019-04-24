package org.synchronizer.spotify;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
import org.synchronizer.spotify.configuration.SpotifyConfiguration;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.spotify.OAuth2RestTemplateSpotify;
import org.synchronizer.spotify.ui.UIText;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

@Configuration
@EnableAsync
public class SpotifySynchronizerConfiguration {
    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(25);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("ss-background");
        executor.initialize();
        return executor;
    }

    @Bean
    public TaskExecutor uiTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("ui-background");
        executor.initialize();
        return executor;
    }

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
