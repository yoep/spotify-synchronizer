package be.studios.yoep.spotify.synchronizer;

import be.studios.yoep.spotify.synchronizer.authorization.*;
import be.studios.yoep.spotify.synchronizer.authorization.filters.AuthorizationFilter;
import be.studios.yoep.spotify.synchronizer.authorization.filters.OpenIdConnectFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private OAuth2RestTemplate authorizationTemplate;
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Bean
    public AuthorizationFilter spotifyAuthorizationFilter() {
        return new AuthorizationFilter(authorizationService);
    }

    @Bean
    public OpenIdConnectFilter openIdConnectFilter() {
        return new OpenIdConnectFilter(LoginController.ENDPOINT, authorizationTemplate, authenticationSuccessHandler);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .regexMatchers("/.*").authenticated()
                .anyRequest().permitAll()
                .and().exceptionHandling()
                .and()
                .addFilterBefore(openIdConnectFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new AuthorizationFilter(authorizationService), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(LoginController.ENDPOINT);
    }
}
