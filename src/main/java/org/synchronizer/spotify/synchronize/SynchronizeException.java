package org.synchronizer.spotify.synchronize;

public class SynchronizeException extends RuntimeException {
    public SynchronizeException(String message) {
        super(message);
    }

    public SynchronizeException(String message, Throwable cause) {
        super(message, cause);
    }
}
