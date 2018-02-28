package be.studios.yoep.spotify.synchronizer.ui;

public class ViewNotFoundException extends RuntimeException {
    public ViewNotFoundException(String view, Exception ex) {
        super("View '" + view + "' couldn't be found", ex);
    }
}
