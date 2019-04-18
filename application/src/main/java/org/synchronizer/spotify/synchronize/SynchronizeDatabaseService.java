package org.synchronizer.spotify.synchronize;

import org.synchronizer.spotify.domain.TrackType;
import org.synchronizer.spotify.domain.entities.AlbumInfoEntity;
import org.synchronizer.spotify.domain.entities.TrackInfoEntity;
import org.synchronizer.spotify.domain.repositories.TrackRepository;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SpotifyTrack;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class SynchronizeDatabaseService {
    private final TrackRepository trackRepository;
    private final AlbumService albumService;

    @Async
    @Transactional
    public void sync(MusicTrack musicTrack) {
        Assert.notNull(musicTrack, "musicTrack cannot be null");
        TrackType type = musicTrack instanceof SpotifyTrack ? TrackType.SPOTIFY : TrackType.LOCAL;

        try {
            boolean trackExists = trackRepository.findByTitleAndArtistAndType(musicTrack.getTitle(), musicTrack.getArtist(), type).isPresent();

            if (!trackExists) {
                trackRepository.save(TrackInfoEntity.builder()
                        .title(musicTrack.getTitle())
                        .artist(musicTrack.getArtist())
                        .uri(musicTrack.getUri())
                        .type(type)
                        .album(getAlbum(musicTrack.getAlbum()))
                        .build());
            }
        } catch (DataRetrievalFailureException ex) {
            log.error("Failed to sync " + musicTrack, ex);
        }
    }

    private AlbumInfoEntity getAlbum(Album album) {
        return albumService.synchronizeAlbum(album);
    }
}
