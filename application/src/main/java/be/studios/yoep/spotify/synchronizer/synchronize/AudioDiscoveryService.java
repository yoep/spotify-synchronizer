package be.studios.yoep.spotify.synchronizer.synchronize;

import be.studios.yoep.spotify.synchronizer.synchronize.model.LocalAlbum;
import be.studios.yoep.spotify.synchronizer.synchronize.model.LocalTrack;
import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import be.studios.yoep.spotify.synchronizer.tika.Mp3Properties;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
public class AudioDiscoveryService {
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
        Parser parser = new Mp3Parser();
        Metadata metadata = new Metadata();

        try {
            parser.parse(FileUtils.openInputStream(file), new BodyContentHandler(), metadata, new ParseContext());
            LocalTrack track = LocalTrack.builder()
                    .file(file)
                    .artist(metadata.get(Mp3Properties.CREATOR))
                    .album(LocalAlbum.builder()
                            .name(metadata.get(Mp3Properties.ALBUM))
                            .build())
                    .title(metadata.get(Mp3Properties.TITLE))
                    .build();

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

    private boolean isAudioFile(File file) {
        return extensions.indexOf(FilenameUtils.getExtension(file.getName()).toLowerCase()) != -1;
    }
}
