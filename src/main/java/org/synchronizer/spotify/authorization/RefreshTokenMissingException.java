package org.synchronizer.spotify.authorization;

public class RefreshTokenMissingException extends RuntimeException {
    public RefreshTokenMissingException() {
        super("Refresh token is missing! Unable to refresh the access token.");
    }
}
