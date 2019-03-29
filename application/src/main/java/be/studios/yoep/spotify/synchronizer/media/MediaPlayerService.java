package be.studios.yoep.spotify.synchronizer.media;

import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import be.studios.yoep.spotify.synchronizer.views.components.PlayerComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
     * Get the current track that is being played.
     *
     * @return Returns the current track of the player.
     */
    public Optional<MusicTrack> getCurrentTrack() {
        return playerComponent.getCurrentTrack();
    }

    /**
     * Set the action to execute when the on next button is pressed.
     *
     * @param onNext The on next action to execute.
     */
    public void setOnNext(Consumer<MusicTrack> onNext) {
        playerComponent.setOnNext(onNext);
    }

    /**
     * Set the action to execute when the on previous button is pressed.
     *
     * @param onPrevious The on previous action to execute.
     */
    public void setOnPrevious(Consumer<MusicTrack> onPrevious) {
        playerComponent.setOnPrevious(onPrevious);
    }
}
