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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
public class AudioDiscoveryService {
    private static final List<String> extensions = Collections.singletonList("mp3");

    @Async
    public CompletableFuture<List<MusicTrack>> scanDirectory(File directory) {
        Assert.notNull(directory, "directory cannot be null");
        File[] files = directory.listFiles();
        Parser parser = new Mp3Parser();
        List<MusicTrack> musicTracks = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    if (isAudioFile(file)) {
                        Metadata metadata = new Metadata();

                        try {
                            parser.parse(FileUtils.openInputStream(file), new BodyContentHandler(), metadata, new ParseContext());
                            LocalTrack track = LocalTrack.builder()
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

                            musicTracks.add(track);
                        } catch (Exception ex) {
                            log.error("Unable to read audio file", ex);
                        }
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(musicTracks);
    }

    private boolean isAudioFile(File file) {
        return extensions.indexOf(FilenameUtils.getExtension(file.getName()).toLowerCase()) != -1;
    }
}
