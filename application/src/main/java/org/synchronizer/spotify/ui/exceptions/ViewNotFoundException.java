package org.synchronizer.spotify.ui.exceptions;

public class ViewNotFoundException extends RuntimeException {
    public ViewNotFoundException(String view, Exception ex) {
        super("View '" + view + "' couldn't be found", ex);
    }
}
