package be.studios.yoep.spotify.synchronizer.authorization;

import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    @Retryable(value = AccessTokenNotAvailable.class, interceptor = "spotifyRetry")
    public void getAccessTokenWhenAvailable() {
        throw new AccessTokenNotAvailable();
    }
}
