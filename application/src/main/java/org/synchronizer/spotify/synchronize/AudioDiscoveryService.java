package org.synchronizer.spotify.synchronize;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.synchronizer.spotify.synchronize.model.LocalAlbum;
import org.synchronizer.spotify.synchronize.model.LocalTrack;
import org.synchronizer.spotify.synchronize.model.MusicTrack;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
public class AudioDiscoveryService {
    private static final Pattern TRACK_NUMBER_PATTERN = Pattern.compile("([0-9]+)/([0-9]*)");
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
                .map(this::processAudioFile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return CompletableFuture.completedFuture(musicTracks);
    }

    private MusicTrack processAudioFile(File file) {
        try {
            Mp3File mp3File = new Mp3File(file);
            LocalTrack track;

            if (mp3File.hasId3v1Tag()) {
                track = processMetadataV1(file, mp3File);
            } else {
                track = processMetadataV2(file, mp3File);
            }

            if (track.getAlbum() != null && track.getArtist() != null && track.getTitle() != null) {
                log.debug("Found mp3 with {}", track);
            } else {
                log.warn("Missing mp3 metadata for {} file, with data {}", file, track);
            }

            return track;
        } catch (Exception ex) {
            log.error("Unable to read audio file " + file, ex);
        }

        return null;
    }

    private LocalTrack processMetadataV1(File file, Mp3File mp3File) {
        ID3v1 metadata = mp3File.getId3v1Tag();
        return LocalTrack.builder()
                .file(file)
                .artist(metadata.getArtist())
                .album(LocalAlbum.builder()
                        .name(metadata.getAlbum())
                        .build())
                .title(metadata.getTitle())
                .trackNumber(getTrackNumberV1(metadata))
                .build();
    }

    private LocalTrack processMetadataV2(File file, Mp3File mp3File) {
        ID3v2 metadata = mp3File.getId3v2Tag();
        return LocalTrack.builder()
                .file(file)
                .artist(metadata.getArtist())
                .album(LocalAlbum.builder()
                        .name(metadata.getAlbum())
                        .image(metadata.getAlbumImage())
                        .build())
                .title(metadata.getTitle())
                .trackNumber(getTrackNumberV2(metadata))
                .build();
    }

    private boolean isAudioFile(File file) {
        return extensions.indexOf(FilenameUtils.getExtension(file.getName()).toLowerCase()) != -1;
    }

    private static Integer getTrackNumberV1(ID3v1 metadata) {
        return Optional.ofNullable(metadata.getTrack())
                .filter(StringUtils::isNotEmpty)
                .map(Integer::parseInt)
                .orElse(null);
    }

    private static Integer getTrackNumberV2(ID3v2 metadata) {
        return Optional.ofNullable(metadata.getTrack())
                .map(TRACK_NUMBER_PATTERN::matcher)
                .filter(Matcher::matches)
                .map(e -> e.group(1))
                .map(Integer::parseInt)
                .orElse(null);
    }
}
