package org.synchronizer.spotify.views.model;

import org.synchronizer.spotify.SpotifySynchronizer;
import org.synchronizer.spotify.common.PlayerState;
import org.synchronizer.spotify.media.MediaPlayerService;
import org.synchronizer.spotify.media.PlayerStateChangeListener;
import org.synchronizer.spotify.media.TrackChangeListener;
import org.synchronizer.spotify.views.components.AlbumOverviewComponent;
import org.synchronizer.spotify.views.components.AlbumTrackComponent;

public class AlbumTrackListenerImpl implements AlbumTrackListener {
    private final AlbumOverviewComponent albumOverviewComponent;
    private final AlbumTrackComponent trackComponent;
    private final MediaPlayerService mediaPlayerService;

    private TrackChangeListener trackChangeListener;
    private PlayerStateChangeListener playerStateChangeListener;

    public AlbumTrackListenerImpl(AlbumOverviewComponent albumOverviewComponent, AlbumTrackComponent trackComponent) {
        this.albumOverviewComponent = albumOverviewComponent;
        this.trackComponent = trackComponent;

        this.mediaPlayerService = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(MediaPlayerService.class);
        initialize();
    }

    @Override
    public void onPlay() {
        mediaPlayerService.play(trackComponent.getSyncTrack());
        trackComponent.setPlaybackState(true);
        albumOverviewComponent.setPlaybackState(true);
        subscribeListenersToMediaPlayer();
    }

    @Override
    public void onPlayPause() {
        if (mediaPlayerService.getCurrentPlayerState() == PlayerState.PAUSED) {
            mediaPlayerService.play();
        } else {
            mediaPlayerService.pause();
        }
    }

    private void initialize() {
        trackChangeListener = (oldTrack, newTrack) -> onTrackChanged();
        playerStateChangeListener = (oldState, newState) -> onPlayerStateChanged(newState);
    }

    private void onTrackChanged() {
        trackComponent.setPlaybackState(false);
        albumOverviewComponent.setPlaybackState(false);
        unsubscribeListenersToMediaPlayer();
    }

    private void onPlayerStateChanged(PlayerState newState) {
        trackComponent.updatePlayPauseIcon(newState);
        albumOverviewComponent.updatePlayPauseIcon(newState);
    }

    private void subscribeListenersToMediaPlayer() {
        mediaPlayerService.addPlayerStateChangeListener(playerStateChangeListener);
        mediaPlayerService.addTrackChangeListener(trackChangeListener);
    }

    private void unsubscribeListenersToMediaPlayer() {
        mediaPlayerService.removePlayerStateChangeListener(playerStateChangeListener);
        mediaPlayerService.removeTrackChangeListener(trackChangeListener);
    }
}
