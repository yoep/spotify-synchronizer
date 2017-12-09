package be.studios.yoep.spotify.synchronizer.managers;

public class PrimaryWindowNotAvailableException extends Exception {
    public PrimaryWindowNotAvailableException() {
        super("Primary window is not available.");
    }
}
