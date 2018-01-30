package be.studios.yoep.spotify.synchronizer.authorization.filters;

import be.studios.yoep.spotify.synchronizer.authorization.AuthorizationService;
import be.studios.yoep.spotify.synchronizer.authorization.SpotifyToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class AuthorizationFilter extends GenericFilterBean {
    private final AuthorizationService authorizationService;

    public AuthorizationFilter(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        SpotifyToken spotifyToken = authorizationService.getToken();

        securityContext.setAuthentication(spotifyToken);

        chain.doFilter(request, response);
    }
}
