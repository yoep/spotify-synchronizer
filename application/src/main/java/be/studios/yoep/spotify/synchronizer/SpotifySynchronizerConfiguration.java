package be.studios.yoep.spotify.synchronizer;

import be.studios.yoep.spotify.synchronizer.authorization.AuthorizationService;
import be.studios.yoep.spotify.synchronizer.authorization.SpotifyAccessTokenProvider;
import be.studios.yoep.spotify.synchronizer.configuration.SpotifyConfiguration;
import be.studios.yoep.spotify.synchronizer.ui.UIText;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Arrays.asList;

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
    public Module javaTimeModule() {
        return new JavaTimeModule()
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")))
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")))
                .addSerializer(LocalDate.class, new LocalDateSerializer(ofPattern("yyyy-MM-dd")))
                .addDeserializer(LocalDate.class, new LocalDateDeserializer(ofPattern("yyyy-MM-dd")));
    }

    @Bean
    public Module jdk8Module() {
        return new Jdk8Module();
    }

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder(List<Module> modules) {
        return new Jackson2ObjectMapperBuilder()
                .modules(modules)
                .serializationInclusion(JsonInclude.Include.NON_EMPTY)
                .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .featuresToEnable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
                .featuresToEnable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(UIText.DIRECTORY + "splash");
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
                                                  Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
        OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(spotifyAuthorization, new DefaultOAuth2ClientContext(new DefaultAccessTokenRequest()));
        oAuth2RestTemplate.setAccessTokenProvider(new AccessTokenProviderChain(asList(
                new SpotifyAccessTokenProvider(authorizationService),
                new AuthorizationCodeAccessTokenProvider(),
                new ImplicitAccessTokenProvider(),
                new ResourceOwnerPasswordAccessTokenProvider(),
                new ClientCredentialsAccessTokenProvider())));
        List<HttpMessageConverter<?>> messageConverters = oAuth2RestTemplate.getMessageConverters();
        messageConverters.add(new MappingJackson2HttpMessageConverter(jackson2ObjectMapperBuilder.build()));
        return oAuth2RestTemplate;
    }
}
