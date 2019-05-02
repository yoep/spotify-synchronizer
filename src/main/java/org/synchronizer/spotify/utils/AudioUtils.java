package org.synchronizer.spotify.utils;

import com.mpatric.mp3agic.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.synchronizer.spotify.synchronize.SynchronizeException;
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

    public static boolean updateFileMetadata(LocalTrack track) {
        try {
            File file = track.getFile();
            Mp3File mp3File = new Mp3File(file);

            // always add or update the V2 tag
            updateMetadataV2(mp3File, track);

            // only update the V1 tag if it's already present, otherwise, ignore this tag
            if (mp3File.hasId3v1Tag()) {
                updateMetadataV1(mp3File, track);
            }

            String tempFile = getTempFilePath(file);
            mp3File.save(tempFile);
            replaceFileWithTempFile(file, tempFile);
            log.debug("Updated metadata of " + file);
            return true;
        } catch (Exception ex) {
            log.error("Failed to update audio file metadata", ex);
        }

        return false;
    }

    private static LocalTrack processMetadataV1(File file, Mp3File mp3File) {
        ID3v1 metadata = mp3File.getId3v1Tag();
        return LocalTrack.builder()
                .file(file)
                .artist(metadata.getArtist())
                .album(LocalAlbum.builder()
                        .name(metadata.getAlbum())
                        .genre(getGenre(metadata.getGenre()))
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
                        .genre(getGenre(metadata.getGenre()))
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
        log.debug("Updating ID3v1 tag information for " + track);
        ID3v1 metadata = file.getId3v1Tag();
        Album album = track.getAlbum();

        //update track info
        metadata.setTrack(getTrackNumber(track));
        metadata.setArtist(track.getArtist());
        metadata.setTitle(track.getTitle());

        //update album info
        metadata.setAlbum(album.getName());
        metadata.setTrack(getTrackNumber(track));
        metadata.setGenre(getGenre(album));
    }

    private static void updateMetadataV2(Mp3File file, LocalTrack track) {
        log.debug("Updating ID3v2 tag information for " + track);
        ID3v2 metadata = Optional.ofNullable(file.getId3v2Tag())
                .orElseGet(AudioUtils::createMetadataV2Tag);
        Album album = track.getAlbum();

        //Windows 7 is unable to read v24 tags, so we downgrade them to v23
        if (metadata instanceof ID3v24Tag)
            metadata = downgradeMetadataV2(file);

        //update track info
        metadata.setTrack(getTrackNumber(track));
        metadata.setArtist(track.getArtist());
        metadata.setTitle(track.getTitle());

        //update album info
        metadata.setAlbum(album.getName());
        metadata.setAlbumImage(album.getImage(), album.getImageMimeType());
        metadata.setGenre(getGenre(album));
    }

    private static ID3v2 downgradeMetadataV2(Mp3File file) {
        file.removeId3v2Tag();

        return new ID3v23Tag();
    }

    private static ID3v2 createMetadataV2Tag() {
        log.debug("Creating ID3v2 tag as it didn't yet exist for the audio file");
        return new ID3v23Tag();
    }

    private static String getTrackNumber(LocalTrack track) {
        return Optional.ofNullable(track.getTrackNumber())
                .map(String::valueOf)
                .orElse(null);
    }

    private static Integer getGenre(Album album) {
        return Optional.ofNullable(album.getGenre())
                .map(ID3v1Genres::matchGenreDescription)
                .orElse(-1);
    }

    private static String getGenre(int genre) {
        if (genre == -1 || genre > ID3v1Genres.GENRES.length)
            return null;

        return ID3v1Genres.GENRES[genre];
    }

    private static String getTempFilePath(File file) {
        String path = FilenameUtils.getFullPath(file.getAbsolutePath());
        String name = FilenameUtils.removeExtension(file.getName());
        String extension = FilenameUtils.getExtension(file.getName());

        return path + name + "_temp." + extension;
    }

    private static boolean replaceFileWithTempFile(File file, String tempFilePath) {
        File tempFile = new File(tempFilePath);

        if (file.delete()) {
            if (!tempFile.renameTo(file)) {
                throw new SynchronizeException("Rename of file " + tempFilePath + " to " + file + " failed");
            }

            return true;
        } else {
            log.error("Failed to delete file " + file);
            //delete temp file
            tempFile.delete();
        }

        return false;
    }
}
