package be.studios.yoep.spotify.synchronizer.authorization;

public class AccessTokenNotAvailable extends RuntimeException {
    public AccessTokenNotAvailable() {
        super("Access token not available");
    }
}
