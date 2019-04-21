package org.synchronizer.spotify.synchronize;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.synchronizer.spotify.synchronize.model.LocalTrack;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
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
    public void updateFileMetadata(LocalTrack track) {
        Assert.notNull(track, "track cannot be null");
        AudioUtils.updateFileMetadata(track);
    }

    private boolean isAudioFile(File file) {
        return extensions.indexOf(FilenameUtils.getExtension(file.getName()).toLowerCase()) != -1;
    }
}
