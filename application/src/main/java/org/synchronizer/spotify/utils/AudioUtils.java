package org.synchronizer.spotify.utils;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.LocalAlbum;
import org.synchronizer.spotify.synchronize.model.LocalTrack;

import java.io.File;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class AudioUtils {
    private static final Pattern TRACK_NUMBER_PATTERN = Pattern.compile("([0-9]+)/([0-9]*)");

    private AudioUtils() {
    }

    public static LocalTrack readAudioFile(File file) {
        try {
            Mp3File mp3File = new Mp3File(file);
            LocalTrack track;

            if (mp3File.hasId3v2Tag()) {
                track = processMetadataV2(file, mp3File);
            } else {
                track = processMetadataV1(file, mp3File);
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

    public static void updateFileMetadata(LocalTrack track) {
        try {
            Mp3File mp3File = new Mp3File(track.getFile());

            if (mp3File.hasId3v2Tag()) {
                updateMetadataV2(mp3File, track);
            }
            if (mp3File.hasId3v1Tag()) {
                updateMetadataV1(mp3File, track);
            }

            //TODO: save as temp file first and replace the original afterwards
            mp3File.save(track.getFile().getName());
        } catch (Exception ex) {
            log.error("Failed to update audio file metadata", ex);
        }
    }

    private static LocalTrack processMetadataV1(File file, Mp3File mp3File) {
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

    private static LocalTrack processMetadataV2(File file, Mp3File mp3File) {
        ID3v2 metadata = mp3File.getId3v2Tag();
        return LocalTrack.builder()
                .file(file)
                .artist(metadata.getArtist())
                .album(LocalAlbum.builder()
                        .name(metadata.getAlbum())
                        .image(metadata.getAlbumImage())
                        .imageMimeType(metadata.getAlbumImageMimeType())
                        .build())
                .title(metadata.getTitle())
                .trackNumber(getTrackNumberV2(metadata))
                .build();
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

    private static void updateMetadataV1(Mp3File file, LocalTrack track) {
        ID3v1 metadata = file.getId3v1Tag();

        metadata.setAlbum(track.getAlbum().getName());
        metadata.setTrack(Optional.ofNullable(track.getTrackNumber())
                .map(String::valueOf)
                .orElse(null));
    }

    private static void updateMetadataV2(Mp3File file, LocalTrack track) {
        ID3v2 metadata = file.getId3v2Tag();
        Album album = track.getAlbum();

        metadata.setAlbum(album.getName());
        metadata.setAlbumImage(album.getImage(), album.getImageMimeType());
    }
}
