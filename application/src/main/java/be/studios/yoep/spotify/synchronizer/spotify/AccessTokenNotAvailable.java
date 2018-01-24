package be.studios.yoep.spotify.synchronizer.spotify;

public class AccessTokenNotAvailable extends RuntimeException {
    public AccessTokenNotAvailable() {
        super("Access token not available");
    }
}
