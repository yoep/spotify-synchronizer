package org.synchronizer.spotify.media;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.synchronizer.spotify.synchronize.SynchronizeException;
import org.synchronizer.spotify.synchronize.model.*;
import org.synchronizer.spotify.utils.AudioUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
public class AudioService {
    private static final List<String> extensions = Collections.singletonList("mp3");

    @Async
    public CompletableFuture<List<MusicTrack>> scanDirectory(File directory) {
        Assert.notNull(directory, "directory cannot be null");
        File[] files = directory.listFiles();

        List<MusicTrack> musicTracks = Optional.ofNullable(files)
                .map(Arrays::stream)
                .orElse(Stream.empty())
                .filter(File::isFile)
                .filter(this::isAudioFile)
                .map(AudioUtils::readAudioFile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return CompletableFuture.completedFuture(musicTracks);
    }

    @Async
    public void updateFileMetadata(SyncTrack track) {
        Assert.notNull(track, "track cannot be null");

        try {
            track.setUpdateState(UpdateState.UPDATING);

            LocalTrack localTrack = (LocalTrack) track.getLocalTrack().orElseThrow(() -> new SynchronizeException("Local track is not available for synchronization"));
            MusicTrack spotifyTrack = track.getSpotifyTrack().orElseThrow(() -> new SynchronizeException("Spotify track is not available for synchronization"));
            LocalAlbum localAlbum = (LocalAlbum) localTrack.getAlbum();
            Album spotifyAlbum = spotifyTrack.getAlbum();

            //update track info
            localTrack.setTitle(spotifyTrack.getTitle());
            localTrack.setArtist(spotifyTrack.getArtist());
            localTrack.setTrackNumber(spotifyTrack.getTrackNumber());

            //update album info
            localAlbum.setImage(spotifyAlbum.getImage());
            localAlbum.setImageMimeType(spotifyAlbum.getImageMimeType());
            localAlbum.setName(spotifyAlbum.getName());
            localAlbum.setGenre(Optional.ofNullable(spotifyAlbum.getGenre())
                    .orElse(localAlbum.getGenre()));

            if (AudioUtils.updateFileMetadata(localTrack)) {
                track.setUpdateState(UpdateState.SUCCESS);
            } else {
                track.setUpdateState(UpdateState.FAILED);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            track.setUpdateState(UpdateState.FAILED);
        }
    }

    private boolean isAudioFile(File file) {
        return extensions.indexOf(FilenameUtils.getExtension(file.getName()).toLowerCase()) != -1;
    }
}
