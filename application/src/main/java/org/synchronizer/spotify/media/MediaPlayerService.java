package org.synchronizer.spotify.media;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.synchronizer.spotify.common.PlayerState;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.views.components.PlayerComponent;

import java.util.Optional;
import java.util.function.Consumer;

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

    /**
     * Resume/play the current played track.
     */
    public void play() {
        playerComponent.onPlay();
    }

    /**
     * Pause the current played track.
     */
    public void pause() {
        playerComponent.onPause();
    }

    /**
     * Get the current track that is being played.
     *
     * @return Returns the current track of the player.
     */
    public Optional<MusicTrack> getCurrentTrack() {
        return playerComponent.getCurrentTrack();
    }

    /**
     * Get the current state of the player.
     *
     * @return Returns the current player state.
     */
    public PlayerState getCurrentPlayerState() {
        return playerComponent.getPlayerState();
    }

    /**
     * Add a listener which is invoked when the on next is clicked.
     *
     * @param onNext The listener to subscribe.
     */
    public void addOnNextListener(Consumer<MusicTrack> onNext) {
        playerComponent.setOnNext(onNext);
    }

    /**
     * Add a listener which is invoked when the on previous is clicked.
     *
     * @param onPrevious The listener to subscribe.
     */
    public void addOnPreviousListener(Consumer<MusicTrack> onPrevious) {
        playerComponent.setOnPrevious(onPrevious);
    }

    /**
     * Add a listener which is invoked when the track is being changed.
     *
     * @param onTrackChange The listener to subscribe.
     */
    public void addOnTrackChangeListener(Runnable onTrackChange) {
        playerComponent.setOnTrackChange(onTrackChange);
    }
}
