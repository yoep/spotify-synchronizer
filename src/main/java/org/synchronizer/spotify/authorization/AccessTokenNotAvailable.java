package org.synchronizer.spotify.authorization;

public class AccessTokenNotAvailable extends RuntimeException {
    public AccessTokenNotAvailable() {
        super("Access token not available");
    }
}
