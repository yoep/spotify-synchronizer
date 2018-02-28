package be.studios.yoep.spotify.synchronizer.ui;

public class PrimaryWindowNotAvailableException extends Exception {
    public PrimaryWindowNotAvailableException() {
        super("Primary window is not available.");
    }
}
