package be.studios.yoep.spotify.synchronizer.synchronize;

import be.studios.yoep.spotify.synchronizer.domain.TrackType;
import be.studios.yoep.spotify.synchronizer.domain.entities.AlbumInfoEntity;
import be.studios.yoep.spotify.synchronizer.domain.entities.TrackInfoEntity;
import be.studios.yoep.spotify.synchronizer.domain.repositories.TrackRepository;
import be.studios.yoep.spotify.synchronizer.synchronize.model.Album;
import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import be.studios.yoep.spotify.synchronizer.synchronize.model.SpotifyTrack;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class SynchronizeDatabaseService {
    private final TrackRepository trackRepository;
    private final AlbumService albumService;

    @Async
    @Transactional
    public void sync(List<? extends MusicTrack> musicTracks) {
        musicTracks.forEach(this::sync);
    }

    private void sync(MusicTrack musicTrack) {
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
