package org.synchronizer.spotify.ui.exceptions;

public class PrimaryWindowNotAvailableException extends Exception {
    public PrimaryWindowNotAvailableException() {
        super("Primary window is not available.");
    }
}
