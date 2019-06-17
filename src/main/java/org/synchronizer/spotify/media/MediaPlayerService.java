package org.synchronizer.spotify.media;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.synchronizer.spotify.common.PlayerState;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.views.sections.PlayerSection;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class MediaPlayerService {
    private final List<PlayerStateChangeListener> playerStateChangeListeners = new ArrayList<>();
    private final List<TrackChangeListener> trackChangeListeners = new ArrayList<>();
    private final PlayerSection playerSection;

    @PostConstruct
    private void init() {
        playerSection.setOnPlayerStateChange(oldPlayerState ->
                new ArrayList<>(playerStateChangeListeners).forEach(e -> e.onChange(oldPlayerState, getCurrentPlayerState())));
        playerSection.setOnTrackChange(event ->
                new ArrayList<>(trackChangeListeners).forEach(e -> e.onChange(event.getOldTrack(), event.getNewTrack())));
    }

    /**
     * Play the given music track.
     * Make sure the uri of the music track is not empty.
     *
     * @param track Set the track to play.
     */
    public void play(MusicTrack track) {
        Assert.notNull(track, "track cannot be null");
        Assert.hasText(track.getUri(), "uri cannot be empty");
        play(Collections.singletonList(track));
    }

    /**
     * Play the given list of media tracks.
     *
     * @param tracks The tracks to play.
     */
    public void play(List<MusicTrack> tracks) {
        Assert.notEmpty(tracks, "tracks cannot be empty");
        playerSection.play(tracks, 0);
    }

    /**
     * Play the given tracks in the player.
     *
     * @param tracks     The tracks to play.
     * @param trackIndex The track index to start playing as first.
     */
    public void play(List<MusicTrack> tracks, int trackIndex) {
        Assert.notEmpty(tracks, "tracks cannot be empty");
        playerSection.play(tracks, trackIndex);
    }

    /**
     * Resume/play the current played track.
     */
    public void play() {
        playerSection.onPlay();
    }

    /**
     * Pause the current played track.
     */
    public void pause() {
        playerSection.onPause();
    }

    /**
     * Get the current state of the player.
     *
     * @return Returns the current player state.
     */
    public PlayerState getCurrentPlayerState() {
        return playerSection.getPlayerState();
    }

    /**
     * Add a listener which is invoked when the player state is being changed.
     *
     * @param playerStateChangeListener The player state change listener to add.
     */
    public void addPlayerStateChangeListener(PlayerStateChangeListener playerStateChangeListener) {
        playerStateChangeListeners.add(playerStateChangeListener);
    }

    /**
     * Remove a player state change listener.
     *
     * @param playerStateChangeListener The player state change listener to remove.
     */
    public void removePlayerStateChangeListener(PlayerStateChangeListener playerStateChangeListener) {
        playerStateChangeListeners.remove(playerStateChangeListener);
    }

    /**
     * Add a listener which is invoked when the track is being changed.
     *
     * @param trackChangeListener The track change listener to add.
     */
    public void addTrackChangeListener(TrackChangeListener trackChangeListener) {
        trackChangeListeners.add(trackChangeListener);
    }

    /**
     * Remove a track change listener.
     *
     * @param trackChangeListener The track change listener to remove.
     */
    public void removeTrackChangeListener(TrackChangeListener trackChangeListener) {
        trackChangeListeners.remove(trackChangeListener);
    }
}
