package org.synchronizer.spotify.views.model;

import org.synchronizer.spotify.SpotifySynchronizer;
import org.synchronizer.spotify.common.PlayerState;
import org.synchronizer.spotify.media.MediaPlayerService;
import org.synchronizer.spotify.media.PlayerStateChangeListener;
import org.synchronizer.spotify.media.TrackChangeListener;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.views.components.AlbumOverviewComponent;
import org.synchronizer.spotify.views.components.AlbumTrackComponent;

import java.util.List;
import java.util.stream.Collectors;

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
        List<AlbumTrackComponent> trackComponents = albumOverviewComponent.getAllVisibleTrackComponents();
        List<MusicTrack> tracks = getMusicTracks(trackComponents);

        mediaPlayerService.play(tracks, tracks.indexOf(trackComponent.getSyncTrack()));
        trackComponent.setPlaybackState(true);
        albumOverviewComponent.setPlaybackState(true);

        subscribeStateChangeListener();
        trackComponents.forEach(e -> e.getListeners().forEach(listener -> ((AlbumTrackListenerImpl) listener).subscribeTrackChangeListener()));
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
        trackChangeListener = (oldTrack, newTrack) -> onTrackChanged(newTrack);
        playerStateChangeListener = (oldState, newState) -> onPlayerStateChanged(newState);
    }

    private void onTrackChanged(MusicTrack newTrack) {
        List<AlbumTrackComponent> trackComponents = albumOverviewComponent.getAllVisibleTrackComponents();
        List<MusicTrack> tracks = getMusicTracks(trackComponents);

        if (newTrack != trackComponent.getSyncTrack()) {
            trackComponent.setPlaybackState(false);
            unsubscribeStateChangeListener();
        } else {
            trackComponent.setPlaybackState(true);
            subscribeStateChangeListener();
        }

        boolean isAlbumSong = tracks.contains(newTrack);
        albumOverviewComponent.setPlaybackState(isAlbumSong);

        // check if the new track is still of the same album, if not, unsubscribe all the album listeners
        if (!isAlbumSong)
            trackComponents.forEach(e -> e.getListeners().forEach(listener -> ((AlbumTrackListenerImpl) listener).unsubscribeTrackChangeListener()));
    }

    private void onPlayerStateChanged(PlayerState newState) {
        trackComponent.updatePlayPauseIcon(newState);
        albumOverviewComponent.updatePlayPauseIcon(newState);
    }

    private void subscribeStateChangeListener() {
        mediaPlayerService.addPlayerStateChangeListener(playerStateChangeListener);
    }

    private void subscribeTrackChangeListener() {
        mediaPlayerService.addTrackChangeListener(trackChangeListener);
    }

    private void unsubscribeStateChangeListener() {
        mediaPlayerService.removePlayerStateChangeListener(playerStateChangeListener);
    }

    private void unsubscribeTrackChangeListener() {
        mediaPlayerService.removeTrackChangeListener(trackChangeListener);
    }

    private List<MusicTrack> getMusicTracks(List<AlbumTrackComponent> trackComponents) {
        return trackComponents.stream()
                .map(AlbumTrackComponent::getSyncTrack)
                .collect(Collectors.toList());
    }
}
