package org.synchronizer.spotify.synchronize;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synchronizer.spotify.domain.TrackType;
import org.synchronizer.spotify.domain.entities.AlbumInfoEntity;
import org.synchronizer.spotify.domain.entities.TrackInfoEntity;
import org.synchronizer.spotify.domain.repositories.TrackRepository;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SpotifyTrack;

@Log4j2
@Service
@RequiredArgsConstructor
public class TrackService {
    private final TrackRepository trackRepository;
    private final AlbumService albumService;

    @Transactional
    public void synchronizeTrack(MusicTrack musicTrack) {
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
        } catch (DataAccessException ex) {
            log.error("Failed to sync " + musicTrack, ex);
        }
    }

    private AlbumInfoEntity getAlbum(Album album) {
        return albumService.synchronizeAlbum(album);
    }
}
