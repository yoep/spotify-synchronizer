package be.studios.yoep.spotify.synchronizer.authorization.filters;

import be.studios.yoep.spotify.synchronizer.authorization.AuthenticationSuccessHandler;
import be.studios.yoep.spotify.synchronizer.authorization.OpenIdConnectUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class OpenIdConnectFilter extends AbstractAuthenticationProcessingFilter {
    private final OAuth2RestTemplate restTemplate;

    public OpenIdConnectFilter(String defaultFilterProcessesUrl, OAuth2RestTemplate restTemplate,
                               AuthenticationSuccessHandler authenticationSuccessHandler) {
        super(defaultFilterProcessesUrl);
        this.restTemplate = restTemplate;
        setAuthenticationManager(new NoopAuthenticationManager());
        setAuthenticationSuccessHandler(authenticationSuccessHandler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        OAuth2AccessToken accessToken;

        try {
            accessToken = restTemplate.getAccessToken();
        } catch (OAuth2Exception e) {
            throw new BadCredentialsException("Could not obtain access token", e);
        }

        try {
            String idToken = accessToken.getAdditionalInformation().get("id_token").toString();
            Jwt tokenDecoded = JwtHelper.decode(idToken);
            Map<String, String> authInfo = new ObjectMapper().readValue(tokenDecoded.getClaims(), Map.class);

            OpenIdConnectUserDetails user = new OpenIdConnectUserDetails(authInfo, accessToken);

            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } catch (InvalidTokenException e) {
            throw new BadCredentialsException("Could not obtain user details from token", e);
        }
    }

    private static class NoopAuthenticationManager implements AuthenticationManager {

        @Override
        public Authentication authenticate(Authentication authentication)
                throws AuthenticationException {
            throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
        }

    }
}
