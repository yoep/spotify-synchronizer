package be.studios.yoep.spotify.synchronizer.media;

import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import be.studios.yoep.spotify.synchronizer.views.components.PlayerComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Log4j2
@Service
@RequiredArgsConstructor
public class MediaPlayerService {
    private final PlayerComponent playerComponent;

    /**
     * Play the given music track.
     * Make sure the uri of the music track is not empty.
     *
     * @param track Set the track to play.
     */
    public void play(MusicTrack track) {
        Assert.notNull(track, "track cannot be null");
        Assert.hasText(track.getUri(), "uri cannot be empty");
        playerComponent.play(track);
    }
}
