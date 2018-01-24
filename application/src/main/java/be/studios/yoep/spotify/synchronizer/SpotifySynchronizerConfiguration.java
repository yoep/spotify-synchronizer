package be.studios.yoep.spotify.synchronizer;

import be.studios.yoep.spotify.synchronizer.configuration.SpotifyConfiguration;
import be.studios.yoep.spotify.synchronizer.ui.UIText;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

@Configuration
@EnableAsync
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
        details.setClientId(configuration.getClientId());
        details.setClientSecret(configuration.getClientSecret());
        details.setAccessTokenUri(configuration.getEndpoints().getTokens());
        details.setUserAuthorizationUri(configuration.getEndpoints().getAuthorization());
        details.setPreEstablishedRedirectUri(configuration.getEndpoints().getRedirect());
        details.setUseCurrentUri(false);
        return details;
    }

    @Bean
    public OAuth2RestTemplate spotifyRestTemplate(OAuth2ProtectedResourceDetails spotifyAuthorization) {
        return new OAuth2RestTemplate(spotifyAuthorization, new DefaultOAuth2ClientContext());
    }
}
