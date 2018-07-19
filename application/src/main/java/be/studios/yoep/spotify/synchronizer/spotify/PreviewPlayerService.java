package be.studios.yoep.spotify.synchronizer.spotify;

import be.studios.yoep.spotify.synchronizer.synchronize.model.SpotifyTrack;
import be.studios.yoep.spotify.synchronizer.views.components.PlayerComponent;
import javafx.scene.media.Media;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Log4j2
@Service
@RequiredArgsConstructor
public class PreviewPlayerService {
    private final PlayerComponent playerComponent;

    /**
     * Play the preview for the given spotify track.
     * Make sure the {@link SpotifyTrack#getPreviewUrl()} is not empty before calling this method.
     *
     * @param track Set the track.
     */
    public void play(SpotifyTrack track) {
        Assert.notNull(track, "track cannot be null");
        Assert.hasText(track.getPreviewUrl(), "previewUrl cannot be empty");
        playerComponent.play(new Media(track.getPreviewUrl()));
    }
}
