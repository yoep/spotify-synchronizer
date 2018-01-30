package be.studios.yoep.spotify.synchronizer;

import be.studios.yoep.spotify.synchronizer.configuration.SpotifyConfiguration;
import be.studios.yoep.spotify.synchronizer.ui.UIText;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import java.util.Collections;

@Configuration
@EnableAsync
@EnableOAuth2Client
public class SpotifySynchronizerConfiguration {
    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("ss-background");
        executor.initialize();
        return executor;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(UIText.DIRECTORY + "splash");
        return messageSource;
    }

    @Bean
    public OAuth2ProtectedResourceDetails spotifyAuthorization(SpotifyConfiguration configuration) {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setId("spotify");
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
    public OAuth2RestTemplate spotifyRestTemplate(OAuth2ProtectedResourceDetails spotifyAuthorization) {
        return new OAuth2RestTemplate(spotifyAuthorization, new DefaultOAuth2ClientContext());
    }

    @Bean
    public OAuth2RestTemplate authorizationTemplate(OAuth2ProtectedResourceDetails spotifyAuthorization,
                                                    @Qualifier("oauth2ClientContext") OAuth2ClientContext clientContext) {
        return new OAuth2RestTemplate(spotifyAuthorization, clientContext);
    }
}
