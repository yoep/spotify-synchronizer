package org.synchronizer.spotify.synchronize;

import org.synchronizer.spotify.domain.entities.AlbumInfoEntity;
import org.synchronizer.spotify.domain.repositories.AlbumRepository;
import org.synchronizer.spotify.synchronize.model.Album;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;

    @Transactional
    public AlbumInfoEntity synchronizeAlbum(Album album) {
        AlbumInfoEntity entity = albumRepository.findByName(album.getName())
                .orElse(AlbumInfoEntity.builder()
                        .name(album.getName())
                        .lowResImageUri(album.getLowResImageUri())
                        .highResImageUri(album.getHighResImageUri())
                        .image(album.getImage())
                        .build());

        entity.setImage(album.getImage());
        entity.setLowResImageUri(album.getLowResImageUri());
        entity.setHighResImageUri(album.getHighResImageUri());

        return albumRepository.save(entity);
    }
}
